package service

import edu.udo.cs.sopra.ntf.ConnectionState
import entity.Tile
import view.Refreshable

/**
 * The service that handles anything related to the network.
 */
class NetworkService(val rootService: RootService) : AbstractRefreshingService() {

    companion object {
        /**
         * The URL of the BGW net server.
         */
        const val SERVER_ADDRESS = "sopra.cs.tu-dortmund.de:80/bgw-net/connect"

        /**
         * The internal game ID for Sagani.
         */
        const val GAME_ID = "Sagani"

        /**
         * The secret for the BGW net server.
         */
        const val NETWORK_SECRET = "23_b_tbd"

        /**
         * Whether the network should send debug messages.
         */
        const val DEBUG = false
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
     * @param name The name of the player
     * @param sessionID The session ID of the game to join (as defined by the host)
     *
     * @throws IllegalStateException If the client is already connected or the connection attempt fails
     */
    fun joinGame(name: String, sessionID: String) {
        if (!connect(name)) error("Could not connect to server.")

        connectionState = ConnectionState.CONNECTED
        if (DEBUG) println("[Debug] $name: Joining game with session ID $sessionID")
        client?.joinGame(sessionID, "Greetings from Germany!")
        connectionState = ConnectionState.WAITING_FOR_JOIN_CONFIRMATION
    }


    /**
     * Connects to the server and creates a new game session.
     *
     * @param name The name of the player
     * @param sessionID The session ID of the game (for guests to join)
     *
     * @throws IllegalStateException If the client is already connected or the connection attempt fails
     */
    fun hostGame(name: String, sessionID: String? = null) {
        if (!connect(name)) error("Could not connect to server.")

        connectionState = ConnectionState.CONNECTED
        if (sessionID.isNullOrBlank()) {
            client?.createGame(GAME_ID, "Greetings from Germany!")
            if (DEBUG) println("[Debug] $name: Creating game")
        } else {
            client?.createGame(GAME_ID, sessionID, "Greetings from Germany!")
            if (DEBUG) println("[Debug] $name: Creating game with session ID $sessionID")
        }
        connectionState = ConnectionState.WAITING_FOR_HOST_CONFIRMATION
    }

    /**
     * Disconnects the client from the server, nulls it and updates the [connectionState] to
     * [ConnectionState.DISCONNECTED]. Can safely be called if the client is not connected.
     */
    fun disconnect() {
        client?.apply {
            if (DEBUG) println("[Debug] ${this.playerName}: Disconnecting from server")
            if (sessionID != null) leaveGame("Goodbye!")
            if (isOpen) disconnect()
        }
        client = null
        connectionState = ConnectionState.DISCONNECTED
    }

    /**
     * Creates a connection to the server, sets the [client] if successful and returns <code>true</code> on success.
     *
     * @param name The name of the player.
     *
     * @throws IllegalArgumentException If the [name] is blank.
     * @throws IllegalArgumentException If the [connectionState] is not [ConnectionState.DISCONNECTED] or the client is
     * already connected.
     *
     * @return <code>true</code> if the connection was created successfully, <code>false</code> otherwise.
     */
    private fun connect(name: String): Boolean {
        check(connectionState == ConnectionState.DISCONNECTED) { "Already connected to server." }
        require(name.isNotBlank()) { "You must enter a name." }

        val client = SaganiNetworkClient(name, SERVER_ADDRESS, this)
        if (client.connect()) {
            this.client = client
            if (DEBUG) println("[Debug] $name: Connected to server")
            return true
        }
        if (DEBUG) println("[Debug] $name: Could not connect to server")
        return false
    }
}
