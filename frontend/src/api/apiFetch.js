export async function apiFetch(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        credentials: 'include',
    });

    if (response.status === 401) {
        throw new Error('SESSION_EXPIRED');
    }

    return response;
}