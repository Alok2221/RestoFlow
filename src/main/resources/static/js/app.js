

// Main App Logic
let currentUser = null;

// Initialize app
async function initApp() {
    loadCart();
    checkAuth();
    setupLogout();
}

// Cart Management
function loadCart() {
    const cart = JSON.parse(localStorage.getItem('cart') || '[]');
    updateCartBadge(cart.length);
    return cart;
}

function saveCart(cart) {
    localStorage.setItem('cart', JSON.stringify(cart));
    updateCartBadge(cart.length);
}

function addToCart(dishId, dishName, price) {
    const cart = loadCart();
    const existingItem = cart.find(item => item.dishId === dishId);
    
    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        cart.push({
            dishId,
            dishName,
            price,
            quantity: 1
        });
    }
    
    saveCart(cart);
    showMessage('Dodano do koszyka!', 'success');
    
    // Animate cart badge
    const badge = document.getElementById('cartBadge');
    if (badge) {
        badge.classList.add('animate-pulse');
        setTimeout(() => badge.classList.remove('animate-pulse'), 1000);
    }
}

function removeFromCart(dishId) {
    const cart = loadCart();
    const filtered = cart.filter(item => item.dishId !== dishId);
    saveCart(filtered);
    return filtered;
}

function updateCartQuantity(dishId, quantity) {
    const cart = loadCart();
    const item = cart.find(item => item.dishId === dishId);
    if (item) {
        if (quantity <= 0) {
            return removeFromCart(dishId);
        }
        item.quantity = quantity;
    }
    saveCart(cart);
    return cart;
}

function clearCart() {
    localStorage.removeItem('cart');
    updateCartBadge(0);
}

function updateCartBadge(count) {
    const badge = document.getElementById('cartBadge');
    if (badge) {
        badge.textContent = count;
        badge.style.display = count > 0 ? 'inline' : 'none';
    }
}

// Auth Management
function checkAuth() {
    const token = localStorage.getItem('token');
    const userEmail = localStorage.getItem('userEmail');
    
    if (token && userEmail) {
        loadUserProfile(userEmail);
    } else {
        showAuthNav();
    }
}

async function loadUserProfile(email) {
    try {
        const profile = await api.getProfile(email);
        if (profile) {
            currentUser = profile;
            showUserNav(profile);
        } else {
            showAuthNav();
        }
    } catch (error) {
        console.error('Error loading profile:', error);
        showAuthNav();
    }
}

function showAuthNav() {
    const authNav = document.getElementById('authNav');
    const userNav = document.getElementById('userNav');
    if (authNav) authNav.style.display = 'block';
    if (userNav) userNav.style.display = 'none';
}

function showUserNav(user) {
    const authNav = document.getElementById('authNav');
    const userNav = document.getElementById('userNav');
    const userName = document.getElementById('userName');
    const adminLink = document.getElementById('adminLink');
    
    if (authNav) authNav.style.display = 'none';
    if (userNav) userNav.style.display = 'block';
    if (userName) userName.textContent = `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.email;
    
    // Show admin link for admin users
    if (adminLink && user.role === 'ADMIN') {
        adminLink.style.display = 'block';
    }
}

function setupLogout() {
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', (e) => {
            e.preventDefault();
            logout();
        });
    }
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userEmail');
    currentUser = null;
    clearCart(); // Clear cart on logout
    showAuthNav();
    
    // Determine correct path based on current location
    const currentPath = window.location.pathname;
    let loginPath;
    
    if (currentPath.includes('/pages/')) {
        // We're in pages directory
        loginPath = 'login.html';
    } else {
        // We're in root directory
        loginPath = 'pages/login.html';
    }
    
    // Show logout message
    showMessage('Logged out successfully', 'success');
    
    // Redirect after short delay
    setTimeout(() => {
        window.location.href = loginPath;
    }, 500);
}

// Utility Functions
function getDishImageUrl(dish) {
    if (!dish || !dish.name) {
        return 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop';
    }
    
    // Prefer imageUrl from DB - paths in data.sql match files (e.g. zurek_z_biala_kielbasa.png)
    if (dish.imageUrl && dish.imageUrl.trim() !== '') {
        return dish.imageUrl;
    }
    
    // Fallback: generate file name from dish.name; mapping of Polish characters (ł, ą, ę, ó, ć, ś, ź, ż, ń)
    const plMap = { 'ł':'l','ą':'a','ę':'e','ó':'o','ć':'c','ś':'s','ź':'z','ż':'z','ń':'n','Ł':'l','Ą':'a','Ę':'e','Ó':'o','Ć':'c','Ś':'s','Ź':'z','Ż':'z','Ń':'n' };
    const imageName = dish.name
        .toLowerCase()
        .replace(/[łąęóćśźżń]/g, c => plMap[c] || c)
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .replace(/[^a-z0-9]+/g, '_')
        .replace(/^_+|_+$/g, '');
    
    return `/images/${imageName}.png`;
}

function showMessage(message, type = 'success') {
    const messageDiv = document.createElement('div');
    messageDiv.className = `message message-${type}`;
    messageDiv.textContent = message;
    messageDiv.style.position = 'fixed';
    messageDiv.style.top = '100px';
    messageDiv.style.right = '20px';
    messageDiv.style.zIndex = '9999';
    messageDiv.style.minWidth = '300px';
    
    document.body.appendChild(messageDiv);
    
    setTimeout(() => {
        messageDiv.style.opacity = '0';
        messageDiv.style.transition = 'opacity 0.3s';
        setTimeout(() => messageDiv.remove(), 300);
    }, 3000);
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

function formatTime(timeString) {
    return timeString.substring(0, 5);
}

function formatPrice(price) {
    return typeof price === 'number' ? price.toFixed(2) : parseFloat(price).toFixed(2);
}

/** Maps FoodType enum to human-readable labels (NORMALNE, WEGETARIANSKIE, WEGANSKIE, BEZGLUTENOWE). */
function getFoodTypeName(foodType) {
    if (!foodType) return '';
    const names = {
        'NORMALNE': 'Standard',
        'WEGETARIANSKIE': 'Vegetarian',
        'WEGANSKIE': 'Vegan',
        'BEZGLUTENOWE': 'Gluten-free'
    };
    return names[foodType] || foodType;
}

// Pagination Helper
function createPagination(currentPage, totalPages, onPageChange) {
    const pagination = document.getElementById('pagination');
    if (!pagination) return;
    
    pagination.innerHTML = '';
    
    if (totalPages <= 1) return;
    
    // Previous button
    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${currentPage === 1 ? 'disabled' : ''}`;
    prevLi.innerHTML = `<a class="page-link" href="#">Previous</a>`;
    prevLi.addEventListener('click', (e) => {
        e.preventDefault();
        if (currentPage > 1) onPageChange(currentPage - 1);
    });
    pagination.appendChild(prevLi);
    
    // Page numbers
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);
    
    if (startPage > 1) {
        const firstLi = document.createElement('li');
        firstLi.className = 'page-item';
        firstLi.innerHTML = `<a class="page-link" href="#">1</a>`;
        firstLi.addEventListener('click', (e) => {
            e.preventDefault();
            onPageChange(1);
        });
        pagination.appendChild(firstLi);
        
        if (startPage > 2) {
            const ellipsis = document.createElement('li');
            ellipsis.className = 'page-item disabled';
            ellipsis.innerHTML = `<span class="page-link">...</span>`;
            pagination.appendChild(ellipsis);
        }
    }
    
    for (let i = startPage; i <= endPage; i++) {
        const li = document.createElement('li');
        li.className = `page-item ${i === currentPage ? 'active' : ''}`;
        li.innerHTML = `<a class="page-link" href="#">${i}</a>`;
        li.addEventListener('click', (e) => {
            e.preventDefault();
            onPageChange(i);
        });
        pagination.appendChild(li);
    }
    
    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            const ellipsis = document.createElement('li');
            ellipsis.className = 'page-item disabled';
            ellipsis.innerHTML = `<span class="page-link">...</span>`;
            pagination.appendChild(ellipsis);
        }
        
        const lastLi = document.createElement('li');
        lastLi.className = 'page-item';
        lastLi.innerHTML = `<a class="page-link" href="#">${totalPages}</a>`;
        lastLi.addEventListener('click', (e) => {
            e.preventDefault();
            onPageChange(totalPages);
        });
        pagination.appendChild(lastLi);
    }
    
    // Next button
    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage === totalPages ? 'disabled' : ''}`;
    nextLi.innerHTML = `<a class="page-link" href="#">Next</a>`;
    nextLi.addEventListener('click', (e) => {
        e.preventDefault();
        if (currentPage < totalPages) onPageChange(currentPage + 1);
    });
    pagination.appendChild(nextLi);
}

// Initialize on page load
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initApp);
} else {
    initApp();
}
