import { apiFetch } from "./apiFetch";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export async function getMe() {
    const response = await apiFetch(`${API_BASE_URL}/user/me`, {
        method: 'GET',
        credentials: 'include',
    });

    if (!response.ok) {
        const data = await response.json();
        throw new Error(data.message);
    }

    return await response.json();
}

export async function updateProfileImage(profileImageId) {
    const response = await apiFetch(
        `${API_BASE_URL}/user/me/profile-image`,
        {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify({
                profileImageId,
            }),
        }
    );

    if (!response.ok) {
        const data = await response.json();
        throw new Error(data.message);
    }
}

export async function changePassword(currentPassword, newPassword) {
    const response = await apiFetch(
        `${API_BASE_URL}/user/me/password`,
        {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify({
                currentPassword,
                newPassword,
            }),
        }
    );

    if (!response.ok) {
        const data = await response.json();
        throw new Error(data.message);
    }
}

export async function deleteUser() {
    const response = await apiFetch(`${API_BASE_URL}/user/me`, {
        method: 'DELETE',
        credentials: 'include',
    });

    if (!response.ok) {
        const data = await response.json();
        throw new Error(data.message);
    }
}

export async function updateFinalGoal(finalGoal) {
    const response = await apiFetch(
        `${API_BASE_URL}/user/me/final-goal`,
        {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify({
                finalGoal,
            }),
        }
    );

    if (!response.ok) {
        let message = '최종 목표를 수정하지 못했습니다.';

        try {
            const data = await response.json();
            message = data.message || message;
        } catch {
            // response body가 없는 경우
        }

        throw new Error(message);
    }

    return await response.json();
}