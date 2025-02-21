import axios from 'axios';

//Create axios instance with base url
const api = axios.create({
    baseURL: "http://localhost:8000"
});

//Export api instance
export default api;