// define action on servers state
export const UPDATE_LIST = 'UPDATE_LIST';
export const UPDATE_PRIORITY = 'UPDATE_PRIORITY';
//init state : []

export const reducer = (state, action) => {
    // get the id of the server has priority = 1
    if (action.type === UPDATE_LIST) {
        // payload is list of server: [{}, {}, {}]
        const newList = action.payload;
        newList.forEach((server) => {
            if (!state.find(oldServer => oldServer.id === server.id)) {
                state.push(server);
            }
        });
        localStorage.setItem('servers', JSON.stringify(state));
        return [...state];
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
