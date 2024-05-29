// define action on servers state
export const UPDATE_LIST = 'UPDATE_LIST';
export const DOWN_PRIORITY = 'DOWN_PRIORITY';
export const UP_PRIORITY = 'UP_PRIORITY';
export const UP_HIGHEST_PRIORITY = 'UP_HIGHEST_PRIORITY';

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
    else if (action.type === UP_HIGHEST_PRIORITY) {
        // update highedst priority
        // payload is a server index
        const index = action.payload;
        if (index > 0 && index < state.length) {
            let element = state[index];
            state = [element].concat(state.slice(0, index), state.slice(index + 1));
        }
        localStorage.setItem('servers', JSON.stringify(state));
        return state;
    }
    else if (action.type === UP_PRIORITY) {
        const index = action.payload;
        if (index > 0 && index < state.length) {
            const temp = state[index];
            state[index] = state[index - 1];
            state[index - 1] = temp;
        }
        localStorage.setItem('servers', JSON.stringify(state));
        if (index === 1) {
            return [...state];
        }
        return state;
    }
    else {
        const index = action.payload;
        if (index >= 0 && index < state.length - 1) {
            let temp = state[index];
            state[index] = state[index + 1];
            state[index + 1] = temp;
        }
        localStorage.setItem('servers', JSON.stringify(state));
        if (index === 0) {
            return [...state]
        }
        return state;
    }
}
