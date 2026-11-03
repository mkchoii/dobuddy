import { apiFetch } from "./apiFetch";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export async function getMyStudy() {
    const response = await apiFetch(
        `${API_BASE_URL}/studies/me`,
        {
            method: 'GET',
            credentials: 'include',
        }
    );

    if (!response.ok) {
        let message = '스터디 정보를 불러오지 못했습니다.';

        try {
            const data = await response.json();
            message = data.message || message;
        } catch {
            // 응답 body 없음
        }

        throw new Error(message);
    }

    return await response.json();
}

export async function getStudies(category) {
    const url =
        category === 'ALL'
            ? `${API_BASE_URL}/studies`
            : `${API_BASE_URL}/studies?category=${category}`;

    const response = await apiFetch(url, {
        method: 'GET',
        credentials: 'include',
    });

    if (!response.ok) {
        let message = '스터디 목록을 불러오지 못했습니다.';

        try {
            const data = await response.json();
            message = data.message || message;
        } catch {
            // 응답 body 없음
        }

        throw new Error(message);
    }

    return await response.json();
}

export async function getMatchingGoals(studyId) {
    const response = await apiFetch(
        `${API_BASE_URL}/studies/${studyId}/matching-goals`,
        {
            method: 'GET',
            credentials: 'include',
        }
    );

    if (!response.ok) {
        let message = '연동 가능한 세부목표를 불러오지 못했습니다.';

        try {
            const data = await response.json();
            message = data.message || message;
        } catch {}

        throw new Error(message);
    }

    return await response.json();
}

export async function joinStudy(studyId, goalIds) {
    const response = await apiFetch(
        `${API_BASE_URL}/studies/${studyId}/members`,
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify({ goalIds }),
        }
    );

    if (!response.ok) {
        let message = '스터디 가입에 실패했습니다.';
        let errorCode = null;

        try {
            const data = await response.json();
            message = data.message || message;
            errorCode = data.errorCode ?? data.code ?? null;
        } catch {}

        const error = new Error(message);
        error.status = response.status;
        error.errorCode = errorCode;

        throw error;
    }

    return await response.json();
}

export async function getStudyDashboard(studyId) {
    const response = await apiFetch(
        `${API_BASE_URL}/studies/${studyId}/dashboard`,
        {
            method: 'GET',
            credentials: 'include',
        }
    );

    if (!response.ok) {
        let message = '팀 대시보드를 불러오지 못했습니다.';

        try {
            const data = await response.json();
            message = data.message || message;
        } catch {}

        throw new Error(message);
    }

    return await response.json();
}

export async function getStudyMemberDetail(studyId, studyMemberId) {
    const response = await apiFetch(
        `${API_BASE_URL}/studies/${studyId}/members/${studyMemberId}`,
        {
            method: 'GET',
            credentials: 'include',
        }
    );

    if (!response.ok) {
        let message = '멤버 상세 정보를 불러오지 못했습니다.';

        try {
            const data = await response.json();
            message = data.message || message;
        } catch {
            // 기본 오류 메시지 사용
        }

        throw new Error(message);
    }

    return await response.json();
}

export async function updateStudy(studyId, { studyName, category, maxMembers }) {
    const response = await apiFetch(
        `${API_BASE_URL}/studies/${studyId}`,
        {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify({
                studyName,
                category,
                maxMembers,
            }),
        }
    );

    if (!response.ok) {
        let message = '스터디 수정에 실패했습니다.';

        try {
            const data = await response.json();
            message = data.message || message;
        } catch {
            // 기본 오류 메시지 사용
        }

        throw new Error(message);
    }

    return response.json();
}

export async function deleteStudyMember(studyId, studyMemberId) {
    const response = await apiFetch(
        `${API_BASE_URL}/studies/${studyId}/members/${studyMemberId}`,
        {
            method: 'DELETE',
            credentials: 'include',
        }
    );

    if (!response.ok) {
        throw new Error('멤버 강퇴에 실패했습니다.');
    }

    return response.json();
}

export async function createStudy(studyName, category, maxMembers) {
    const response = await apiFetch(`${API_BASE_URL}/studies`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({
            studyName,
            category,
            maxMembers,
        }),
    });

    if (!response.ok) {
        throw new Error('스터디 개설에 실패했습니다.');
    }

    return response.json();
}

export async function deleteStudy(studyId) {
    const response = await apiFetch(`${API_BASE_URL}/studies/${studyId}`,
        {
            method: 'DELETE',
            credentials: 'include'
        }
    );

    if (!response.ok) {
        throw new Error('스터디 삭제에 실패했습니다.');
    }
}

export async function updateStudyGoal(studyId, goalIds) {
    const response = await apiFetch(`${API_BASE_URL}/studies/${studyId}/goals`,
        {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify({
                goalIds
            })
        }
    );

    if (!response.ok) {
        throw new Error('연동 세부목표 수정에 실패했습니다.');
    }
}

export async function getStudyAvailability(studyId) {
    const response = await apiFetch(`${API_BASE_URL}/studies/${studyId}/availability`,
        {
            method: 'GET',
            credentials: 'include'
        }
    );

    if (!response.ok) {
        throw new Error('스터디 정원 조회에 실패했습니다.');
    }

    return response.json();
}