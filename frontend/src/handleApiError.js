export function handleApiError(error) {
    if (error.message === 'SESSION_EXPIRED') {
        alert('세션이 만료되었습니다. 다시 로그인해주세요.');
        window.location.replace('/login');
        return true;
    }

    return false;
}