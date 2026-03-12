

// API Client
const API_BASE_URL = 'http://localhost:8080/api';

const api = {
    async request(endpoint, options = {}) {
        const url = `${API_BASE_URL}${endpoint}`;
        const config = {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        };
        
        // Add auth token if available
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        
        try {
            const response = await fetch(url, config);
            
            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || `HTTP error! status: ${response.status}`);
            }
            
            // Handle empty responses
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            }
            return null;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    },
    
    // Auth endpoints
    async register(data) {
        return this.request('/auth/register', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    
    async login(data) {
        return this.request('/auth/login', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    
    async getProfile(email) {
        return this.request(`/auth/me?email=${encodeURIComponent(email)}`);
    },
    
    // Menu endpoints
    async getMenu(query = '') {
        const endpoint = query ? `/menu?q=${encodeURIComponent(query)}` : '/menu';
        return this.request(endpoint);
    },
    
    async getMenuByCategory(category) {
        return this.request(`/menu/category/${category}`);
    },
    
    async getAvailableMenu() {
        return this.request('/menu/available');
    },
    
    async getDish(id) {
        return this.request(`/menu/${id}`);
    },
    
    // Order endpoints
    async createOrder(data) {
        return this.request('/orders', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    
    async getOrders() {
        return this.request('/orders');
    },
    
    async getOrderByNumber(orderNumber) {
        return this.request(`/orders/${orderNumber}`);
    },
    
    async updateOrderStatus(id, status) {
        return this.request(`/orders/${id}/status?status=${status}`, {
            method: 'PUT'
        });
    },
    
    // Reservation endpoints
    async createReservation(data) {
        return this.request('/reservations', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    
    async checkAvailability(date, startTime, endTime, peopleCount) {
        const params = new URLSearchParams({
            date: date,
            startTime: startTime,
            endTime: endTime,
            peopleCount: peopleCount
        });
        return this.request(`/reservations/available?${params}`);
    },
    
    async getReservationByNumber(reservationNumber) {
        return this.request(`/reservations/${reservationNumber}`);
    },
    
    async cancelReservation(id) {
        return this.request(`/reservations/${id}`, {
            method: 'DELETE'
        });
    },
    
    async getReservationsByDate(date) {
        return this.request(`/reservations/day/${date}`);
    },
    
    // Event endpoints
    async createEvent(data) {
        return this.request('/events', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    
    async getEventTypes() {
        return this.request('/events/types');
    },
    
    async getEvent(id) {
        return this.request(`/events/${id}`);
    },
    
    // Table endpoints
    async getTables() {
        return this.request('/tables');
    },
    
    async createTable(data) {
        return this.request('/tables', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    
    async updateTable(id, data) {
        return this.request(`/tables/${id}`, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },
    
    async updateTableAvailability(id, available) {
        return this.request(`/tables/${id}/availability?available=${available}`, {
            method: 'PUT'
        });
    },
    
    // Admin endpoints
    async getStatistics() {
        return this.request('/admin/statistics');
    },
    
    async getTodayOrders() {
        return this.request('/admin/orders/today');
    },
    
    async getTodayReservations() {
        return this.request('/admin/reservations/today');
    }
};

if (typeof module !== 'undefined' && module.exports) {
    module.exports = api;
}
