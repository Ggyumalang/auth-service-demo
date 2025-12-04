import axios from 'axios'

const instance = axios.create({
    baseURL: '' // Use relative path or configure base URL if needed
})

instance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('accessToken')
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

export default instance
