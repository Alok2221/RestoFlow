// Auth Page Logic
document.addEventListener('DOMContentLoaded', async () => {
    await initApp();
    setupLoginForm();
    setupRegisterForm();
});

function setupLoginForm() {
    const form = document.getElementById('loginForm');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            await handleLogin();
        });
    }
}

function setupRegisterForm() {
    const form = document.getElementById('registerForm');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            await handleRegister();
        });
    }
}

async function handleLogin() {
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;
    const submitBtn = document.querySelector('#loginForm button[type="submit"]');
    
    if (!email || !password) {
        showMessage('Wypełnij wszystkie pola', 'error');
        return;
    }
    
    try {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Logowanie...';
        
        const response = await api.login({ email, password });
        
        if (response && response.token) {
            localStorage.setItem('token', response.token);
            localStorage.setItem('userEmail', response.email);
            
            showMessage('Zalogowano pomyślnie!', 'success');
            
            setTimeout(() => {
                window.location.href = '../index.html';
            }, 1000);
        } else {
            showMessage('Błąd logowania. Sprawdź dane.', 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        showMessage('Błąd logowania. Sprawdź dane i spróbuj ponownie.', 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="bi bi-box-arrow-in-right me-2"></i>Zaloguj';
    }
}

async function handleRegister() {
    const firstName = document.getElementById('registerFirstName').value;
    const lastName = document.getElementById('registerLastName').value;
    const email = document.getElementById('registerEmail').value;
    const password = document.getElementById('registerPassword').value;
    const phone = document.getElementById('registerPhone').value;
    const address = document.getElementById('registerAddress').value;
    const submitBtn = document.querySelector('#registerForm button[type="submit"]');
    
    if (!firstName || !lastName || !email || !password) {
        showMessage('Wypełnij wszystkie wymagane pola', 'error');
        return;
    }
    
    if (password.length < 6) {
        showMessage('Hasło musi mieć co najmniej 6 znaków', 'error');
        return;
    }
    
    try {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Rejestrowanie...';
        
        const response = await api.register({
            firstName,
            lastName,
            email,
            password,
            phone: phone || null,
            address: address || null
        });
        
        if (response && response.token) {
            localStorage.setItem('token', response.token);
            localStorage.setItem('userEmail', response.email);
            
            showMessage('Rejestracja pomyślna! Zalogowano automatycznie.', 'success');
            
            setTimeout(() => {
                window.location.href = '../index.html';
            }, 1500);
        } else {
            showMessage('Błąd rejestracji. Spróbuj ponownie.', 'error');
        }
    } catch (error) {
        console.error('Register error:', error);
        showMessage('Błąd rejestracji. Email może być już zajęty.', 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="bi bi-person-plus me-2"></i>Zarejestruj';
    }
}
