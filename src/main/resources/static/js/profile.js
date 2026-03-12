// Profile Page Logic
document.addEventListener('DOMContentLoaded', async () => {
    await initApp();
    await loadProfile();
    setupViewOrders();
});

async function loadProfile() {
    const userEmail = localStorage.getItem('userEmail');
    if (!userEmail) {
        window.location.href = 'login.html';
        return;
    }
    
    try {
        const profile = await api.getProfile(userEmail);
        if (profile) {
            displayProfile(profile);
        } else {
            showMessage('Błąd ładowania profilu', 'error');
        }
    } catch (error) {
        console.error('Error loading profile:', error);
        showMessage('Błąd ładowania profilu', 'error');
    }
}

function displayProfile(profile) {
    const container = document.getElementById('profileInfo');
    if (!container) return;
    
    container.innerHTML = `
        <div class="row g-3">
            <div class="col-md-6">
                <label class="form-label text-muted">Imię</label>
                <p class="form-control-plaintext">${profile.firstName || '-'}</p>
            </div>
            <div class="col-md-6">
                <label class="form-label text-muted">Nazwisko</label>
                <p class="form-control-plaintext">${profile.lastName || '-'}</p>
            </div>
            <div class="col-md-6">
                <label class="form-label text-muted">Email</label>
                <p class="form-control-plaintext">${profile.email || '-'}</p>
            </div>
            <div class="col-md-6">
                <label class="form-label text-muted">Telefon</label>
                <p class="form-control-plaintext">${profile.phone || '-'}</p>
            </div>
            <div class="col-12">
                <label class="form-label text-muted">Adres</label>
                <p class="form-control-plaintext">${profile.address || '-'}</p>
            </div>
            <div class="col-12">
                <label class="form-label text-muted">Rola</label>
                <p class="form-control-plaintext">
                    <span class="badge bg-primary">${getRoleName(profile.role)}</span>
                </p>
            </div>
        </div>
    `;
}

function setupViewOrders() {
    const btn = document.getElementById('viewOrdersBtn');
    if (btn) {
        btn.addEventListener('click', async (e) => {
            e.preventDefault();
            await showOrders();
        });
    }
}

async function showOrders() {
    try {
        const orders = await api.getOrders();
        
        if (orders && orders.length > 0) {
            const modal = createOrdersModal(orders);
            document.body.appendChild(modal);
            const bsModal = new bootstrap.Modal(modal);
            bsModal.show();
            
            modal.addEventListener('hidden.bs.modal', () => {
                modal.remove();
            });
        } else {
            showMessage('Brak zamówień', 'info');
        }
    } catch (error) {
        console.error('Error loading orders:', error);
        showMessage('Błąd ładowania zamówień', 'error');
    }
}

function createOrdersModal(orders) {
    const modal = document.createElement('div');
    modal.className = 'modal fade';
    modal.innerHTML = `
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Moje zamówienia</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Numer</th>
                                    <th>Data</th>
                                    <th>Status</th>
                                    <th>Typ</th>
                                    <th>Kwota</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${orders.map(order => `
                                    <tr>
                                        <td>${order.orderNumber}</td>
                                        <td>${formatDate(order.createdAt || order.date)}</td>
                                        <td><span class="badge ${getOrderStatusBadgeClass(order.status)}">${getOrderStatusName(order.status)}</span></td>
                                        <td>${getOrderTypeName(order.type)}</td>
                                        <td>${formatPrice(order.totalAmount)} zł</td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Zamknij</button>
                </div>
            </div>
        </div>
    `;
    return modal;
}

function getRoleName(role) {
    const names = {
        'ADMIN': 'Administrator',
        'PRACOWNIK': 'Pracownik',
        'KLIENT': 'Klient'
    };
    return names[role] || role;
}

function getOrderStatusName(status) {
    const names = {
        'OCZEKUJACE': 'Oczekujące',
        'POTWIERDZONE': 'Potwierdzone',
        'W_PRZYGOTOWANIU': 'W przygotowaniu',
        'GOTOWE': 'Gotowe',
        'DOSTARCZONE': 'Dostarczone',
        'ANULOWANE': 'Anulowane'
    };
    return names[status] || status;
}

function getOrderStatusBadgeClass(status) {
    const classes = {
        'OCZEKUJACE': 'bg-warning',
        'POTWIERDZONE': 'bg-info',
        'W_PRZYGOTOWANIU': 'bg-primary',
        'GOTOWE': 'bg-success',
        'DOSTARCZONE': 'bg-success',
        'ANULOWANE': 'bg-danger'
    };
    return classes[status] || 'bg-secondary';
}

function getOrderTypeName(type) {
    const names = {
        'DOSTAWA': 'Dostawa',
        'ODBIOR_OSOBISTY': 'Odbiór osobisty'
    };
    return names[type] || type;
}
