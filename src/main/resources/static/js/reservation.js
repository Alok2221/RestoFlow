

// Reservation Page Logic
document.addEventListener('DOMContentLoaded', async () => {
    await initApp();
    setupReservationForm();
    setupDateRestrictions();
    loadMyReservations();
});

// Restaurant hours
const RESTAURANT_OPENING_TIME = '12:00';
const RESTAURANT_CLOSING_TIME = '23:00';
const MIN_RESERVATION_DURATION_MINUTES = 60;
const MAX_RESERVATION_DURATION_HOURS = 4;

function setupDateRestrictions() {
    const dateInput = document.getElementById('reservationDate');
    const startTimeInput = document.getElementById('startTime');
    const endTimeInput = document.getElementById('endTime');
    
    if (dateInput) {
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        dateInput.min = tomorrow.toISOString().split('T')[0];
    }
    
    if (startTimeInput) {
        startTimeInput.min = RESTAURANT_OPENING_TIME;
        startTimeInput.max = RESTAURANT_CLOSING_TIME;
    }
    
    if (endTimeInput) {
        endTimeInput.min = RESTAURANT_OPENING_TIME;
        endTimeInput.max = RESTAURANT_CLOSING_TIME;
    }
    
    // Validate time when changed
    if (startTimeInput && endTimeInput) {
        startTimeInput.addEventListener('change', validateReservationTimes);
        endTimeInput.addEventListener('change', validateReservationTimes);
    }
}

function validateReservationTimes() {
    const startTime = document.getElementById('startTime').value;
    const endTime = document.getElementById('endTime').value;
    
    if (!startTime || !endTime) return;
    
    const start = new Date(`2000-01-01T${startTime}`);
    const end = new Date(`2000-01-01T${endTime}`);
    const opening = new Date(`2000-01-01T${RESTAURANT_OPENING_TIME}`);
    const closing = new Date(`2000-01-01T${RESTAURANT_CLOSING_TIME}`);
    
    if (start < opening) {
        showMessage(`Godzina rozpoczęcia nie może być wcześniejsza niż ${RESTAURANT_OPENING_TIME}`, 'error');
        document.getElementById('startTime').value = RESTAURANT_OPENING_TIME;
        return;
    }
    
    if (end > closing) {
        showMessage(`Godzina zakończenia nie może być późniejsza niż ${RESTAURANT_CLOSING_TIME}`, 'error');
        document.getElementById('endTime').value = RESTAURANT_CLOSING_TIME;
        return;
    }
    
    if (start >= end) {
        showMessage('Godzina rozpoczęcia musi być wcześniejsza niż godzina zakończenia', 'error');
        return;
    }
    
    const durationMinutes = (end - start) / (1000 * 60);
    if (durationMinutes < MIN_RESERVATION_DURATION_MINUTES) {
        showMessage(`Minimalny czas rezerwacji to ${MIN_RESERVATION_DURATION_MINUTES} minut`, 'error');
        return;
    }
    
    if (durationMinutes > MAX_RESERVATION_DURATION_HOURS * 60) {
        showMessage(`Maksymalny czas rezerwacji to ${MAX_RESERVATION_DURATION_HOURS} godziny`, 'error');
        return;
    }
}

function setupReservationForm() {
    const form = document.getElementById('reservationForm');
    const checkBtn = document.getElementById('checkAvailabilityBtn');
    const submitBtn = document.getElementById('submitReservationBtn');
    
    if (checkBtn) {
        checkBtn.addEventListener('click', async () => {
            await checkAvailability();
        });
    }
    
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            await submitReservation();
        });
    }
}

async function checkAvailability() {
    const date = document.getElementById('reservationDate').value;
    const startTime = document.getElementById('startTime').value;
    const endTime = document.getElementById('endTime').value;
    const peopleCount = parseInt(document.getElementById('peopleCount').value);
    const resultDiv = document.getElementById('availabilityResult');
    const checkBtn = document.getElementById('checkAvailabilityBtn');
    
    if (!date || !startTime || !endTime || !peopleCount) {
        showMessage('Wypełnij wszystkie wymagane pola', 'error');
        return;
    }
    
    // Validate time constraints
    validateReservationTimes();
    if (startTime < RESTAURANT_OPENING_TIME || endTime > RESTAURANT_CLOSING_TIME) {
        resultDiv.innerHTML = `
            <div class="alert alert-warning">
                <i class="bi bi-exclamation-triangle me-2"></i>
                Restauracja jest otwarta od ${RESTAURANT_OPENING_TIME} do ${RESTAURANT_CLOSING_TIME}
            </div>
        `;
        return;
    }
    
    try {
        checkBtn.disabled = true;
        checkBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Sprawdzanie...';
        
        const available = await api.checkAvailability(date, startTime, endTime, peopleCount);
        
        if (available) {
            resultDiv.innerHTML = `
                <div class="alert alert-success">
                    <i class="bi bi-check-circle me-2"></i>
                    Stoliki są dostępne dla podanych parametrów!
                </div>
            `;
        } else {
            resultDiv.innerHTML = `
                <div class="alert alert-warning">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    Brak dostępnych stolików dla podanych parametrów. Spróbuj innej daty lub godziny.
                </div>
            `;
        }
    } catch (error) {
        console.error('Availability check error:', error);
        resultDiv.innerHTML = `
            <div class="alert alert-danger">
                <i class="bi bi-x-circle me-2"></i>
                Błąd podczas sprawdzania dostępności. Spróbuj ponownie.
            </div>
        `;
    } finally {
        checkBtn.disabled = false;
        checkBtn.innerHTML = '<i class="bi bi-search me-2"></i>Sprawdź dostępność';
    }
}

async function submitReservation() {
    const date = document.getElementById('reservationDate').value;
    const startTime = document.getElementById('startTime').value;
    const endTime = document.getElementById('endTime').value;
    const peopleCount = parseInt(document.getElementById('peopleCount').value);
    const notes = document.getElementById('notes').value;
    const submitBtn = document.getElementById('submitReservationBtn');
    
    if (!date || !startTime || !endTime || !peopleCount) {
        showMessage('Wypełnij wszystkie wymagane pola', 'error');
        return;
    }
    
    // Validate time constraints
    if (startTime < RESTAURANT_OPENING_TIME) {
        showMessage(`Rezerwacja może rozpocząć się najwcześniej o ${RESTAURANT_OPENING_TIME}`, 'error');
        return;
    }
    
    if (endTime > RESTAURANT_CLOSING_TIME) {
        showMessage(`Rezerwacja musi zakończyć się najpóźniej o ${RESTAURANT_CLOSING_TIME}`, 'error');
        return;
    }
    
    const start = new Date(`2000-01-01T${startTime}`);
    const end = new Date(`2000-01-01T${endTime}`);
    const durationMinutes = (end - start) / (1000 * 60);
    
    if (durationMinutes < MIN_RESERVATION_DURATION_MINUTES) {
        showMessage(`Minimalny czas rezerwacji to ${MIN_RESERVATION_DURATION_MINUTES} minut`, 'error');
        return;
    }
    
    if (durationMinutes > MAX_RESERVATION_DURATION_HOURS * 60) {
        showMessage(`Maksymalny czas rezerwacji to ${MAX_RESERVATION_DURATION_HOURS} godziny`, 'error');
        return;
    }
    
    const reservationData = {
        date: date,
        startTime: startTime,
        endTime: endTime,
        peopleCount: peopleCount,
        notes: notes || null
    };
    
    try {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Przetwarzanie...';
        
        const reservation = await api.createReservation(reservationData);
        
        showMessage(`Rezerwacja utworzona! Numer: ${reservation.reservationNumber}`, 'success');
        
        // Reset form
        document.getElementById('reservationForm').reset();
        document.getElementById('availabilityResult').innerHTML = '';
        
        setTimeout(() => {
            loadMyReservations();
        }, 1000);
    } catch (error) {
        console.error('Reservation error:', error);
        let errorMessage = 'Błąd podczas tworzenia rezerwacji. Spróbuj ponownie.';
        if (error.message) {
            errorMessage = error.message;
        } else if (error.response) {
            try {
                const errorData = await error.response.json();
                errorMessage = errorData.message || errorMessage;
            } catch (e) {
                errorMessage = error.response.statusText || errorMessage;
            }
        }
        showMessage(errorMessage, 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="bi bi-calendar-check me-2"></i>Zarezerwuj Stolik';
    }
}

async function loadMyReservations() {
    const section = document.getElementById('myReservationsSection');
    const list = document.getElementById('reservationsList');
    
    if (!section || !list) return;
    
    try {
        const today = new Date().toISOString().split('T')[0];
        const reservations = await api.getReservationsByDate(today);
        
        if (reservations && reservations.length > 0) {
            section.style.display = 'block';
            list.innerHTML = reservations.map((res, index) => `
                <div class="col-md-6">
                    <div class="card reservation-card animate-fade-in" style="animation-delay: ${index * 0.1}s">
                        <div class="card-body">
                            <h5 class="card-title">Rezerwacja ${res.reservationNumber}</h5>
                            <p class="mb-2"><i class="bi bi-calendar me-2"></i>${formatDate(res.date)}</p>
                            <p class="mb-2"><i class="bi bi-clock me-2"></i>${formatTime(res.startTime)} - ${formatTime(res.endTime)}</p>
                            <p class="mb-2"><i class="bi bi-people me-2"></i>${res.peopleCount} osób</p>
                            <p class="mb-2">
                                <span class="badge ${getStatusBadgeClass(res.status)}">${getStatusName(res.status)}</span>
                            </p>
                            ${res.status !== 'ANULOWANA' ? `
                                <button class="btn btn-danger btn-sm" onclick="cancelReservation(${res.id})">
                                    <i class="bi bi-x-circle me-2"></i>Anuluj
                                </button>
                            ` : ''}
                        </div>
                    </div>
                </div>
            `).join('');
        } else {
            section.style.display = 'none';
        }
    } catch (error) {
        console.error('Error loading reservations:', error);
    }
}

async function cancelReservation(id) {
    if (!confirm('Czy na pewno chcesz anulować tę rezerwację?')) {
        return;
    }
    
    try {
        await api.cancelReservation(id);
        showMessage('Rezerwacja anulowana', 'success');
        loadMyReservations();
    } catch (error) {
        console.error('Cancel error:', error);
        showMessage('Błąd podczas anulowania rezerwacji', 'error');
    }
}

function getStatusName(status) {
    const names = {
        'POTWIERDZONA': 'Potwierdzona',
        'ANULOWANA': 'Anulowana',
        'ZAKONCZONA': 'Zakończona'
    };
    return names[status] || status;
}

function getStatusBadgeClass(status) {
    const classes = {
        'POTWIERDZONA': 'bg-success',
        'ANULOWANA': 'bg-danger',
        'ZAKONCZONA': 'bg-secondary'
    };
    return classes[status] || 'bg-secondary';
}
