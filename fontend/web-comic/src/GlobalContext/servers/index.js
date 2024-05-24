// define action on servers state
export const UPDATE_LIST = 'UPDATE_LIST';
export const UPDATE_PRIORITY = 'UPDATE_PRIORITY';

//init state : []

export const reducer = (state, action) => {
    // get the id of the server has priority = 1
    let priorId;
    if (state && state.length > 0) {
        priorId = state.find((server) => server.priority === 1).id
    }

    if (action.type === UPDATE_LIST) {
        // payload is list of server: [{}, {}, {}]
        const newList = action.payload;
        newList.forEach((server, index) => {
            if (priorId) {
                if (server.id === priorId) {
                    server.priority = 1;
                }
                else {
                    server.priority = 0;
                }
            }
            else {
                if (index === 0) {
                    server.priority = 1;
                }
                else {
                    server.priority = 0;
                }
            }
        });
        if (!newList.find(server => server.priority === 1)) {
            newList[0].priority = 1;
        }
        localStorage.setItem('servers', JSON.stringify(newList));
        return newList;
    }
    else {
        // update priority
        // payload is a server object
        state.forEach((server) => {
            if (server.priority === 1) {
                server.priority = 0;
            }
            if (server.id === action.payload.id) {
                server.priority = 1;
            }
        });
        localStorage.setItem('servers', JSON.stringify(state));
        return [...state];
    }
}
