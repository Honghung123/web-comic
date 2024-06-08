import { useEffect, useReducer, useState } from 'react';
import Context from './Context';

import { reducer as serverReducer, UPDATE_LIST } from './servers';
import axios from 'axios';
import { toast } from 'react-toastify';

function Provider({ children }) {

    let availableServers = JSON.parse(localStorage.getItem('servers')) || [];
    const [servers, serversDispatch] = useReducer(serverReducer, availableServers);
    const [currentPage, setCurrentPage] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // lay danh sach cac serverE tu backend
        axios.get(`${process.env.REACT_APP_API_URL}/comic/crawler-plugins`).then(response => {
            const responseData = response.data;
            if (responseData.statusCode === 200) {
                serversDispatch({
                    type: UPDATE_LIST,
                    payload: responseData.data
                })
                setLoading(false);
            }
            else {
                //thong bao loi
                toast.error('Có lỗi xảy ra. Vui lòng thử lại sau!', { toastId: 500 });
            }
        })
            .catch(err => {
                console.log(err);
                toast.error('Có lỗi xảy ra. Vui lòng thử lại sau!', { toastId: 500 });
            })
    }, [])


    // Dam bao phai load duoc list server truoc khi render cac item con
    if (loading) {
        return <></>
    }

    return (
        <Context.Provider value={{
            servers, serversDispatch, currentPage, setCurrentPage
        }}>
            {children}
        </Context.Provider>
    );
}

export default Provider;