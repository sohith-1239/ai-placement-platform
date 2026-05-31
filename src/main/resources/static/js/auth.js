let isLogin = true;

function toggleAuth() {
    isLogin = !isLogin;
    document.getElementById('formTitle').innerText = isLogin ? 'Sign In' : 'Create Account';
    document.getElementById('submitBtn').innerText = isLogin ? 'Login' : 'Sign Up';
    document.getElementById('toggleText').innerText = isLogin ? "Don't have an account?" : "Already have an account?";
    document.getElementById('nameGroup').classList.toggle('d-none');
}

document.getElementById('authForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = document.getElementById('submitBtn');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Processing...';
    
    const email = document.getElementById('emailInput').value;
    const password = document.getElementById('passwordInput').value;
    const name = document.getElementById('nameInput').value;
    
    const alertBox = document.getElementById('authAlert');
    alertBox.className = 'alert mt-3 d-none';
    
    try {
        if(isLogin) {
            const res = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });
            const data = await res.json();
            if(res.ok) {
                localStorage.setItem('token', data.token);
                localStorage.setItem('user', JSON.stringify(data));
                window.location.href = '/dashboard.html';
            } else {
                alertBox.textContent = data.message || "Invalid credentials.";
                alertBox.className = 'alert alert-danger mt-3 d-block';
            }
        } else {
            const res = await fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, email, password, role: "student" })
            });
            if(res.ok) {
                toggleAuth();
                alertBox.textContent = "Registration successful! Please log in.";
                alertBox.className = 'alert alert-success mt-3 d-block';
            } else {
                const data = await res.json();
                alertBox.textContent = data.message || "Registration failed.";
                alertBox.className = 'alert alert-danger mt-3 d-block';
            }
        }
    } catch (err) {
        alertBox.textContent = "Network error. Please make sure backend is running.";
        alertBox.className = 'alert alert-danger mt-3 d-block';
    } finally {
        btn.disabled = false;
        btn.innerHTML = isLogin ? 'Login' : 'Sign Up';
    }
});
