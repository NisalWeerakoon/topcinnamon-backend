function showForm(formType) {
    // Update active tab
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');

    // Show selected form
    document.querySelectorAll('.form').forEach(form => {
        form.classList.remove('active');
    });
    if (formType === 'login') {
        document.getElementById('loginForm').classList.add('active');
    } else {
        document.getElementById('signupForm').classList.add('active');
    }
}

// Handle Login Form
document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = e.target.querySelector('input[type="email"]').value;
    const password = e.target.querySelector('input[type="password"]').value;

    try {
        const response = await fetch('http://localhost:8081/api/auth/authenticateuser', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email,
                password,
                isSignup: false
            })
        });

        const data = await response.json();
        showMessage(data.message, response.ok ? 'success' : 'error');
    } catch (error) {
        showMessage('Error connecting to server', 'error');
    }
});

// Handle Signup Form
document.getElementById('signupForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = {
        email: e.target.querySelector('input[type="email"]').value,
        password: e.target.querySelector('input[type="password"]').value,
        firstName: e.target.querySelectorAll('input[type="text"]')[0].value,
        lastName: e.target.querySelectorAll('input[type="text"]')[1].value,
        mobile: e.target.querySelector('input[type="tel"]').value,
        gender: e.target.querySelector('select').value,
        location: e.target.querySelectorAll('input[type="text"]')[2].value,
        isSignup: true
    };

    try {
        const response = await fetch('http://localhost:8081/api/auth/authenticateuser', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        const data = await response.json();
        showMessage(data.message, response.ok ? 'success' : 'error');
    } catch (error) {
        showMessage('Error connecting to server', 'error');
    }
});

function showMessage(message, type) {
    // Remove existing message
    const existingMessage = document.querySelector('.message');
    if (existingMessage) {
        existingMessage.remove();
    }

    // Create new message
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${type}`;
    messageDiv.textContent = message;

    // Add message to page
    document.querySelector('.form.active').appendChild(messageDiv);

    // Remove message after 3 seconds
    setTimeout(() => {
        messageDiv.remove();
    }, 3000);
}