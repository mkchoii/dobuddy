const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export async function checkEmail(email) {
    const response = await fetch(`${API_BASE_URL}/auth/email-check`, {
        method: 'POST',

        headers: {
            'Content-Type': 'application/json',
        },

        body: JSON.stringify({
            email: email,
        }),
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message);
    }

    return data;
}

export async function signup(email, password) {
    const response = await fetch(`${API_BASE_URL}/signup`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({
            email,
            password
        }),
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message);
    }

    return data;
}

export async function login(email, password) {
    const response = await fetch(`${API_BASE_URL}/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({
            email,
            password
        }),
    });

    const data = await response.json();

    if (!response.ok) {
        const error = new Error(data.message);
        error.code = data.code;
        error.status = response.status;

        throw error;
    }

    return data;
}

export async function logout() {
    const response = await fetch(`${API_BASE_URL}/logout`, {
        method: 'POST',
        credentials: 'include',
    });

    if (!response.ok) {
        throw new Error('로그아웃에 실패했습니다.');
    }
}

export async function resetPassword(email) {
    const response = await fetch(`${API_BASE_URL}/login/new-password`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email }),
    });

    const data = await response.json();

    if (!response.ok) {
        const error = new Error(data.message);
        error.code = data.code;
        error.attemptCount = data.attemptCount;

        throw error;
    }

    return data;
}
