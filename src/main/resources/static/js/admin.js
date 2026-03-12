

// Admin Page Logic
document.addEventListener('DOMContentLoaded', async () => {
    await initApp();
    
    // Check if user is admin
    const userEmail = localStorage.getItem('userEmail');
    if (!userEmail) {
        window.location.href = 'login.html';
        return;
    }
    
    try {
        const profile = await api.getProfile(userEmail);
        if (profile.role !== 'ADMIN') {
            window.location.href = '../index.html';
            return;
        }
    } catch (error) {
        console.error('Error checking admin access:', error);
        window.location.href = '../index.html';
        return;
    }
    
    await loadStatistics();
    await loadTodayOrders();
    await loadTodayReservations();
    await loadTables();
    setupTableManagement();
});

async function loadStatistics() {
    try {
        const stats = await api.getStatistics();
        // Backend zwraca: ordersCount, totalSales, reservationsCount
        document.getElementById('todayOrders').textContent = stats.ordersCount ?? 0;
        document.getElementById('todayReservations').textContent = stats.reservationsCount ?? 0;
        document.getElementById('todayRevenue').textContent = formatPrice(stats.totalSales ?? 0) + ' zł';
    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

async function loadTodayOrders() {
    try {
        const orders = await api.getTodayOrders();
        const container = document.getElementById('ordersList');
        
        if (!container) return;
        
        if (orders && orders.length > 0) {
            container.innerHTML = `
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Numer</th>
                                <th>Data</th>
                                <th>Status</th>
                                <th>Typ</th>
                                <th>Kwota</th>
                                <th>Akcje</th>
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
                                    <td>
                                        <select class="form-select form-select-sm" onchange="updateOrderStatus(${order.id}, this.value)">
                                            <option value="OCZEKUJACE" ${order.status === 'OCZEKUJACE' ? 'selected' : ''}>Oczekujące</option>
                                            <option value="POTWIERDZONE" ${order.status === 'POTWIERDZONE' ? 'selected' : ''}>Potwierdzone</option>
                                            <option value="W_PRZYGOTOWANIU" ${order.status === 'W_PRZYGOTOWANIU' ? 'selected' : ''}>W przygotowaniu</option>
                                            <option value="GOTOWE" ${order.status === 'GOTOWE' ? 'selected' : ''}>Gotowe</option>
                                            <option value="DOSTARCZONE" ${order.status === 'DOSTARCZONE' ? 'selected' : ''}>Dostarczone</option>
                                            <option value="ANULOWANE" ${order.status === 'ANULOWANE' ? 'selected' : ''}>Anulowane</option>
                                        </select>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `;
        } else {
            container.innerHTML = '<p class="text-muted">Brak zamówień na dziś</p>';
        }
    } catch (error) {
        console.error('Error loading orders:', error);
        document.getElementById('ordersList').innerHTML = '<p class="text-danger">Błąd ładowania zamówień</p>';
    }
}

async function loadTodayReservations() {
    try {
        const reservations = await api.getTodayReservations();
        const container = document.getElementById('reservationsList');
        
        if (!container) return;
        
        if (reservations && reservations.length > 0) {
            container.innerHTML = `
                <div class="row g-3">
                    ${reservations.map((res, index) => `
                        <div class="col-md-6">
                            <div class="card animate-fade-in" style="animation-delay: ${index * 0.1}s">
                                <div class="card-body">
                                    <h6 class="card-title">${res.reservationNumber}</h6>
                                    <p class="mb-1"><i class="bi bi-calendar me-2"></i>${formatDate(res.date)}</p>
                                    <p class="mb-1"><i class="bi bi-clock me-2"></i>${formatTime(res.startTime)} - ${formatTime(res.endTime)}</p>
                                    <p class="mb-1"><i class="bi bi-people me-2"></i>${res.peopleCount} osób</p>
                                    <p class="mb-0">
                                        <span class="badge ${getReservationStatusBadgeClass(res.status)}">${getReservationStatusName(res.status)}</span>
                                    </p>
                                </div>
                            </div>
                        </div>
                    `).join('')}
                </div>
            `;
        } else {
            container.innerHTML = '<p class="text-muted">Brak rezerwacji na dziś</p>';
        }
    } catch (error) {
        console.error('Error loading reservations:', error);
        document.getElementById('reservationsList').innerHTML = '<p class="text-danger">Błąd ładowania rezerwacji</p>';
    }
}

async function loadTables() {
    try {
        const tables = await api.getTables();
        const container = document.getElementById('tablesList');
        
        if (!container) return;
        
        if (tables && tables.length > 0) {
            container.innerHTML = `
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Numer</th>
                                <th>Pojemność</th>
                                <th>Lokalizacja</th>
                                <th>Dostępność</th>
                                <th>Status</th>
                                <th>Akcje</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${tables.map(table => `
                                <tr>
                                    <td>${table.tableNumber}</td>
                                    <td>${table.capacity}</td>
                                    <td>${getLocationName(table.location) || '-'}</td>
                                    <td>
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" 
                                                   ${table.available ? 'checked' : ''} 
                                                   onchange="toggleTableAvailability(${table.id}, this.checked)">
                                        </div>
                                    </td>
                                    <td>
                                        <span class="badge ${table.active ? 'bg-success' : 'bg-secondary'}">
                                            ${table.active ? 'Aktywny' : 'Nieaktywny'}
                                        </span>
                                    </td>
                                    <td>
                                        <button class="btn btn-sm btn-primary" onclick="editTable(${table.id})">
                                            <i class="bi bi-pencil"></i>
                                        </button>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `;
        } else {
            container.innerHTML = '<p class="text-muted">Brak stolików</p>';
        }
    } catch (error) {
        console.error('Error loading tables:', error);
        document.getElementById('tablesList').innerHTML = '<p class="text-danger">Błąd ładowania stolików</p>';
    }
}

function setupTableManagement() {
    const form = document.getElementById('addTableForm');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            await addTable();
        });
    }
}

async function addTable() {
    const tableNumber = document.getElementById('tableNumber').value;
    const capacity = parseInt(document.getElementById('tableCapacity').value);
    const location = document.getElementById('tableLocation').value;
    const available = document.getElementById('tableAvailable').checked;
    
    if (!tableNumber || !capacity || !location) {
        showMessage('Wypełnij wszystkie wymagane pola', 'error');
        return;
    }
    
    try {
        const tableData = {
            tableNumber: tableNumber,
            capacity: capacity,
            location: location,
            available: available,
            active: true
        };
        
        await api.createTable(tableData);
        
        showMessage('Stolik dodany pomyślnie', 'success');
        
        const modal = bootstrap.Modal.getInstance(document.getElementById('addTableModal'));
        modal.hide();
        
        form.reset();
        await loadTables();
    } catch (error) {
        console.error('Error adding table:', error);
        showMessage('Błąd podczas dodawania stolika', 'error');
    }
}

async function toggleTableAvailability(id, available) {
    try {
        await api.updateTableAvailability(id, available);
        showMessage(`Dostępność stolika ${available ? 'włączona' : 'wyłączona'}`, 'success');
        await loadTables();
    } catch (error) {
        console.error('Error updating table availability:', error);
        showMessage('Błąd podczas aktualizacji dostępności', 'error');
    }
}

async function editTable(id) {
    try {
        const tables = await api.getTables();
        const table = tables.find(t => t.id === id);
        
        if (!table) {
            showMessage('Nie znaleziono stolika', 'error');
            return;
        }
        
        // Simple edit - you can enhance this with a modal
        const newCapacity = prompt('Nowa pojemność:', table.capacity);
        if (newCapacity && parseInt(newCapacity) > 0) {
            const newLocation = prompt('Lokalizacja (SALA_GLOWNA, TARAS, SALON_VIP, BAR):', table.location || 'SALA_GLOWNA');
            if (!newLocation || !['SALA_GLOWNA','TARAS','SALON_VIP','BAR'].includes(newLocation.trim())) {
                showMessage('Nieprawidłowa lokalizacja', 'error');
                return;
            }
            await api.updateTable(id, {
                tableNumber: table.tableNumber,
                capacity: parseInt(newCapacity),
                location: newLocation.trim(),
                available: table.available,
                active: table.active
            });
            
            showMessage('Stolik zaktualizowany', 'success');
            await loadTables();
        }
    } catch (error) {
        console.error('Error editing table:', error);
        showMessage('Błąd podczas edycji stolika', 'error');
    }
}

async function updateOrderStatus(id, status) {
    try {
        await api.updateOrderStatus(id, status);
        showMessage('Status zamówienia zaktualizowany', 'success');
        await loadTodayOrders();
        await loadStatistics();
    } catch (error) {
        console.error('Error updating order status:', error);
        showMessage('Błąd podczas aktualizacji statusu', 'error');
    }
}

function getLocationName(loc) {
    const names = { 'TARAS': 'Taras', 'SALA_GLOWNA': 'Sala główna', 'SALON_VIP': 'Salon VIP', 'BAR': 'Bar' };
    return (loc && names[loc]) ? names[loc] : loc;
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

function getReservationStatusName(status) {
    const names = {
        'POTWIERDZONA': 'Potwierdzona',
        'ANULOWANA': 'Anulowana',
        'ZREALIZOWANA': 'Zrealizowana',
        'NIESTAWIONY': 'Niestawiony'
    };
    return names[status] || status;
}

function getReservationStatusBadgeClass(status) {
    const classes = {
        'POTWIERDZONA': 'bg-success',
        'ANULOWANA': 'bg-danger',
        'ZREALIZOWANA': 'bg-secondary',
        'NIESTAWIONY': 'bg-warning text-dark'
    };
    return classes[status] || 'bg-secondary';
}
