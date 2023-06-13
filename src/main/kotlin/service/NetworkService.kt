package service

import edu.udo.cs.sopra.ntf.ConnectionState

/**
 * The service that handles anything related to the network.
 */
class NetworkService(private val rootService: RootService) : AbstractRefreshingService() {

    companion object {
        /**
         * The URL of the BGW net server.
         */
        const val SERVER_ADDRESS = "sopra.cs.tu-dortmund.de:80/bgw-net/connect"

        /**
         * The internal game ID.
         */
        const val GAME_ID = "GAME_ID"
    }

    /**
     * The current network state.
     */
    var connectionState = ConnectionState.DISCONNECTED
        set(value) {
            field = value
            onAllRefreshables {
                refreshAfterConnectionStateChange(value)
            }
        }

    /**
     * The client that is connected to the server. Nullable for offline mode.
     */
    var client: SaganiNetworkClient? = null
        private set

    /**
     * Connect to the server and joins a game session as a guest player.
     *
     * @param secret The server secret
     * @param name The name of the player
     * @param sessionID The session ID of the game to join (as defined by the host)
     *
     * @throws IllegalStateException If the client is already connected or the connection attempt fails
     */
    fun joinGame(secret: String, name: String, sessionID: String) {
        if (!connect(secret, name)) {
            error("Could not connect to server.")
        }

        connectionState = ConnectionState.CONNECTED
        client?.joinGame(sessionID, "Greetings from Germany!")
        connectionState = ConnectionState.WAITING_FOR_JOIN_CONFIRMATION
    }


    /**
     * Connects to the server and creates a new game session.
     *
     * @param secret The server secret
     * @param name The name of the player
     * @param sessionID The session ID of the game (for guests to join)
     *
     * @throws IllegalStateException If the client is already connected or the connection attempt fails
     */
    fun hostGame(secret: String, name: String, sessionID: String? = null) {
        if (!connect(secret, name)) {
            error("Could not connect to server.")
        }

        connectionState = ConnectionState.CONNECTED
        if (sessionID.isNullOrBlank()) {
            client?.createGame(GAME_ID, "Greetings from Germany!")
        } else {
            client?.createGame(GAME_ID, sessionID, "Greetings from Germany!")
        }
        connectionState = ConnectionState.WAITING_FOR_HOST_CONFIRMATION
    }

    /**
     * Disconnects the client from the server, nulls it and updates the [connectionState] to
     * [ConnectionState.DISCONNECTED]. Can safely be called if the client is not connected.
     */
    fun disconnect() {
        client?.apply {
            if (sessionID != null) leaveGame("Goodbye!")
            if (isOpen) disconnect()
        }
        client = null
        connectionState = ConnectionState.DISCONNECTED
    }

    /**
     * Creates a connection to the server, sets the [client] if successful and returns <code>true</code> on success.
     *
     * @param secret The secret of the server.
     * @param name The name of the player.
     *
     * @throws IllegalArgumentException If the [secret] or the [name] is blank.
     * @throws IllegalArgumentException If the [connectionState] is not [ConnectionState.DISCONNECTED] or the client is
     * already connected.
     *
     * @return <code>true</code> if the connection was created successfully, <code>false</code> otherwise.
     */
    private fun connect(secret: String, name: String): Boolean {
        check(connectionState == ConnectionState.DISCONNECTED) { "Already connected to server." }
        require(secret.isNotBlank()) { "You must enter a secret." }
        require(name.isNotBlank()) { "You must enter a name." }

        val client = SaganiNetworkClient(name, SERVER_ADDRESS, secret, this)
        if (client.connect()) {
            this.client = client
            return true
        }
        return false
    }
}
