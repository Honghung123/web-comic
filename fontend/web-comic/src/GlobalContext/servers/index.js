// define action on servers state
export const UPDATE_LIST = 'UPDATE_LIST';
export const UPDATE_PRIORITY = 'UPDATE_PRIORITY';

//init state : []

export const reducer = (state, action) => {
    // get the name of the server has priority = 1
    let name;
    if (state && state.length > 0) {
        name = state.find((server) => server.priority === 1).name
    }

    if (action.type === UPDATE_LIST) {
        // payload is list of server: [{}, {}, {}]
        const newList = action.payload;
        newList.forEach((server, index) => {
            if (name) {
                if (server.name === name) {
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
        localStorage.setItem('servers', JSON.stringify(newList));
        return newList;
    }
    else {
        // update priority
        // payload is a server object
        state.forEach((server, index) => {
            if (server.priority === 1) {
                server.priority = 0;
            }
            if (server.name === action.payload.name) {
                server.priority = 1;
            }
        });
        localStorage.setItem('servers', JSON.stringify(state));
        return [...state];
    }
}
