import axios from "axios";


const request = axios.create({
    baseURL: 'http://localhost:8080'
})


export const get = async (url, options = {}) => {
    const response = await request.get(url, options)
    return response.data;
}

export default request;

export * from './StorageUtil';