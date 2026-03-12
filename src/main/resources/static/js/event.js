

// Event Page Logic
// Restaurant hours
const RESTAURANT_OPENING_TIME = '12:00';
const RESTAURANT_CLOSING_TIME = '23:00';

document.addEventListener('DOMContentLoaded', async () => {
    await initApp();
    await loadEventTypes();
    setupEventForm();
    setupDateRestrictions();
});

function setupDateRestrictions() {
    const dateInput = document.getElementById('eventDate');
    const timeInput = document.getElementById('eventTime');
    
    if (dateInput) {
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        dateInput.min = tomorrow.toISOString().split('T')[0];
    }
    
    if (timeInput) {
        timeInput.min = RESTAURANT_OPENING_TIME;
        timeInput.max = RESTAURANT_CLOSING_TIME;
        timeInput.addEventListener('change', validateEventTime);
    }
}

function validateEventTime() {
    const timeInput = document.getElementById('eventTime');
    if (!timeInput || !timeInput.value) return;
    
    const time = timeInput.value;
    if (time < RESTAURANT_OPENING_TIME) {
        showMessage(`Wydarzenie może rozpocząć się najwcześniej o ${RESTAURANT_OPENING_TIME}`, 'error');
        timeInput.value = RESTAURANT_OPENING_TIME;
        return;
    }
    
    if (time > RESTAURANT_CLOSING_TIME) {
        showMessage(`Wydarzenie może rozpocząć się najpóźniej o ${RESTAURANT_CLOSING_TIME}`, 'error');
        timeInput.value = RESTAURANT_CLOSING_TIME;
        return;
    }
}

async function loadEventTypes() {
    try {
        const types = await api.getEventTypes();
        const select = document.getElementById('eventType');
        
        if (select && types) {
            select.innerHTML = '<option value="">-- Wybierz typ --</option>' +
                types.map(type => `
                    <option value="${type}">${getEventTypeName(type)}</option>
                `).join('');
        }
    } catch (error) {
        console.error('Error loading event types:', error);
    }
}


function setupEventForm() {
    const form = document.getElementById('eventForm');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            await submitEvent();
        });
    }
}

async function submitEvent() {
    const form = document.getElementById('eventForm');
    const eventDate = document.getElementById('eventDate').value;
    const eventTime = document.getElementById('eventTime').value;
    const eventType = document.getElementById('eventType').value;
    const theme = document.getElementById('theme').value;
    const decorationPackage = document.getElementById('decorationPackage').value;
    const cake = document.getElementById('cake').checked;
    const entertainment = document.getElementById('entertainment').value;
    const notes = document.getElementById('eventNotes').value;
    
    if (!eventDate || !eventTime || !eventType) {
        showMessage('Wypełnij wszystkie wymagane pola (data, godzina, typ wydarzenia)', 'error');
        return;
    }
    
    // Validate time constraints
    if (eventTime < RESTAURANT_OPENING_TIME) {
        showMessage(`Wydarzenie może rozpocząć się najwcześniej o ${RESTAURANT_OPENING_TIME}`, 'error');
        return;
    }
    
    if (eventTime > RESTAURANT_CLOSING_TIME) {
        showMessage(`Wydarzenie może rozpocząć się najpóźniej o ${RESTAURANT_CLOSING_TIME}`, 'error');
        return;
    }
    
    const eventData = {
        type: eventType,
        date: eventDate,
        time: eventTime,
        theme: theme || null,
        decorationPackage: decorationPackage || null,
        cake: cake,
        entertainment: entertainment || null,
        notes: notes || null
    };
    
    try {
        const submitBtn = form.querySelector('button[type="submit"]');
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Przetwarzanie...';
        
        const event = await api.createEvent(eventData);
        
        showMessage(`Wydarzenie utworzone! ID: ${event.id}`, 'success');
        
        // Reset form
        form.reset();
        await loadEventTypes();
        setupDateRestrictions();
    } catch (error) {
        console.error('Event creation error:', error);
        const errorMessage = error.message || 'Błąd podczas tworzenia wydarzenia. Spróbuj ponownie.';
        showMessage(errorMessage, 'error');
    } finally {
        const submitBtn = form.querySelector('button[type="submit"]');
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="bi bi-balloon me-2"></i>Utwórz Wydarzenie';
    }
}

function getEventTypeName(type) {
    const names = {
        'URODZINY': 'Urodziny',
        'WESELA': 'Wesela',
        'CHRZEST': 'Chrzest',
        'KOMUNIA': 'Komunia',
        'FIRMOWA': 'Firmowa'
    };
    return names[type] || type;
}
