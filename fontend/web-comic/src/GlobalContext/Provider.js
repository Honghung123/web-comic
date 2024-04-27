import { useEffect, useState } from 'react';
import Context from './Context';

import * as request from '../utils'

function Provider({ children }) {

    let availableServers = JSON.parse(localStorage.getItem('servers'));
    const [servers, setServers] = useState(availableServers || []);

    useEffect(() => {
        // lay danh sach cac server tu backend
        request.get('/api/v1/comic/crawler-plugins').then(response => {
            console.log('response', response);
            if (response.statusCode === 200) {
                if (!availableServers) {
                    setServers(response.data);
                    localStorage.setItem('servers', JSON.stringify(response.data));
                }
                else {
                    //them server moi duoc plugin vao
                    if (response.data.length > availableServers.length) {
                        // tam thoi xu ly nhu vay
                        // chua luu tru duoc thu tu uu tien
                        // can phai sua lai ....
                        // cho sua backend r sua cai nay sau
                        setServers(response.data);
                        localStorage.setItem('servers', JSON.stringify(response.data));
                    }
                }
            }
            else {
                //thong bao loi
            }
        });
    }, [])



    return (
        <Context.Provider value={[servers, setServers]}>
            {children}
        </Context.Provider>
    );
}

export default Provider;