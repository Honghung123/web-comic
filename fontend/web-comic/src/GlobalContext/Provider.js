import { useEffect, useReducer, useState } from 'react';
import Context from './Context';

import * as request from '../utils'
import { reducer as serverReducer, UPDATE_LIST, UPDATE_PRIORITY } from './servers';

function Provider({ children }) {

    let availableServers = JSON.parse(localStorage.getItem('servers'));
    const [servers, serversDispatch] = useReducer(serverReducer, availableServers || []);
    const [currentPage, setCurrentPage] = useState('');

    useEffect(() => {
        // lay danh sach cac serverE tu backend
        request.get('/api/v1/comic/crawler-plugins').then(response => {
            if (response.statusCode === 200) {
                if (servers.length === 0) {
                    serversDispatch({
                        type: UPDATE_LIST,
                        payload: response.data
                    })
                }
                else {
                    //them server moi duoc plugin vao
                    if (response.data.length != servers.length) {
                        // tam thoi xu ly nhu vay
                        // chua luu tru duoc thu tu uu tien
                        // can phai sua lai ....
                        // cho sua backend r sua cai nay sau
                        serversDispatch({
                            type: UPDATE_LIST,
                            payload: response.data
                        })
                    }
                }
            }
            else {
                //thong bao loi
            }
        });
    }, [])



    return (
        <Context.Provider value={{
            servers, serversDispatch, currentPage, setCurrentPage
        }}>
            {children}
        </Context.Provider>
    );
}

export default Provider;