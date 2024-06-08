// define action on servers state
export const UPDATE_LIST = 'UPDATE_LIST';
export const UPDATE_PRIORITY = 'UPDATE_PRIORITY';
//init state : []

export const reducer = (state, action) => {
    // get the id of the server has priority = 1
    if (action.type === UPDATE_LIST) {
        // payload is list of server: [{}, {}, {}]
        const newList = action.payload;
        const newState = [];
        // preserve priority of old exist servers
        state.forEach((server) => {
            if (newList.find(newServer => newServer.id === server.id)) {
                newState.push(server);
            }
        });
        // add new servers
        newList.forEach((newServer) => {
            if (!newState.find(server => server.id === newServer.id)) {
                newState.push(newServer);
            }
        })
        localStorage.setItem('servers', JSON.stringify(newState));
        return newState;
    }
    else {
        const { oldPos, newPos } = action.payload;
        const temp = state[oldPos];
        state.splice(oldPos, 1);
        state.splice(newPos, 0, temp);
        localStorage.setItem('servers', JSON.stringify(state));
        if (oldPos === 0 || newPos === 0) {
            return [...state];
        }
        return state;
    }
}
