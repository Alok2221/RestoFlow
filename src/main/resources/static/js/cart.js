

// Cart Page Logic
document.addEventListener('DOMContentLoaded', async () => {
    await initApp();
    displayCart();
    setupCheckout();
    
    // Show/hide delivery address based on order type
    const orderType = document.getElementById('orderType');
    const deliveryAddressGroup = document.getElementById('deliveryAddressGroup');
    
    if (orderType && deliveryAddressGroup) {
        orderType.addEventListener('change', (e) => {
            deliveryAddressGroup.style.display = e.target.value === 'DOSTAWA' ? 'block' : 'none';
        });
    }
});

function displayCart() {
    const cart = loadCart();
    const emptyCart = document.getElementById('emptyCart');
    const cartContent = document.getElementById('cartContent');
    const cartItems = document.getElementById('cartItems');
    
    if (cart.length === 0) {
        emptyCart.style.display = 'block';
        cartContent.style.display = 'none';
        return;
    }
    
    emptyCart.style.display = 'none';
    cartContent.style.display = 'block';
    
    cartItems.innerHTML = cart.map((item, index) => `
        <div class="cart-item animate-fade-in" style="animation-delay: ${index * 0.1}s">
            <div class="row align-items-center">
                <div class="col-md-6">
                    <h6 class="mb-1">${item.dishName}</h6>
                    <p class="text-muted mb-0">${formatPrice(item.price)} zł za sztukę</p>
                </div>
                <div class="col-md-3">
                    <div class="input-group">
                        <button class="btn btn-outline-secondary" type="button" 
                                onclick="updateItemQuantity(${item.dishId}, ${item.quantity - 1})">-</button>
                        <input type="number" class="form-control text-center" 
                               value="${item.quantity}" min="1" 
                               onchange="updateItemQuantity(${item.dishId}, parseInt(this.value))">
                        <button class="btn btn-outline-secondary" type="button" 
                                onclick="updateItemQuantity(${item.dishId}, ${item.quantity + 1})">+</button>
                    </div>
                </div>
                <div class="col-md-2 text-end">
                    <strong>${formatPrice(item.price * item.quantity)} zł</strong>
                </div>
                <div class="col-md-1 text-end">
                    <button class="btn btn-danger btn-sm" onclick="removeItem(${item.dishId})">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </div>
        </div>
    `).join('');
    
    updateTotals(cart);
}

function updateItemQuantity(dishId, quantity) {
    const cart = updateCartQuantity(dishId, quantity);
    displayCart();
}

function removeItem(dishId) {
    removeFromCart(dishId);
    displayCart();
    showMessage('Usunięto z koszyka', 'success');
}

function updateTotals(cart) {
    const subtotal = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    const total = subtotal;
    
    document.getElementById('subtotal').textContent = formatPrice(subtotal) + ' zł';
    document.getElementById('total').textContent = formatPrice(total) + ' zł';
}

function setupCheckout() {
    const checkoutBtn = document.getElementById('checkoutBtn');
    if (checkoutBtn) {
        checkoutBtn.addEventListener('click', async () => {
            await processCheckout();
        });
    }
}

async function processCheckout() {
    const cart = loadCart();
    if (cart.length === 0) {
        showMessage('The cart is empty', 'error');
        return;
    }
    
    const orderType = document.getElementById('orderType').value;
    const deliveryAddress = document.getElementById('deliveryAddress')?.value || '';
    
    if (orderType === 'DOSTAWA' && !deliveryAddress.trim()) {
        showMessage('Please provide a delivery address', 'error');
        return;
    }
    
    const orderData = {
        items: cart.map(item => ({
            dishId: item.dishId,
            quantity: item.quantity,
            notes: ''
        })),
        type: orderType,
        deliveryAddress: orderType === 'DOSTAWA' ? deliveryAddress : null
    };
    
    try {
        const checkoutBtn = document.getElementById('checkoutBtn');
        checkoutBtn.disabled = true;
        checkoutBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Processing...';
        
        const order = await api.createOrder(orderData);
        
        clearCart();
        showMessage(`Order placed! Number: ${order.orderNumber}`, 'success');
        
        setTimeout(() => {
            window.location.href = '../index.html';
        }, 2000);
    } catch (error) {
        console.error('Checkout error:', error);
        showMessage('Error while placing the order. Please try again.', 'error');
        
        const checkoutBtn = document.getElementById('checkoutBtn');
        checkoutBtn.disabled = false;
        checkoutBtn.innerHTML = '<i class="bi bi-check-circle me-2"></i>Place order';
    }
}
