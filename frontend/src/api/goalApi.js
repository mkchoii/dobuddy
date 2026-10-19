const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export async function createGoal(category, goalName) {
    const response = await fetch(`${API_BASE_URL}/goals`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({
            category,
            goalName,
        }),
    });

    if (!response.ok) {
        let message = '목표 생성에 실패했습니다.';

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

export async function updateGoal(goalId, data) {
    const response = await fetch(
        `${API_BASE_URL}/goals/${goalId}`,
        {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify(data),
        }
    );

    if (!response.ok) {
        let message = '세부목표를 수정하지 못했습니다.';

        try {
            const data = await response.json();
            message = data.message || message;
        } catch {}

        throw new Error(message);
    }

    return await response.json();
}

export async function deleteGoal(goalId) {
    const response = await fetch(
        `${API_BASE_URL}/goals/${goalId}`,
        {
            method: 'DELETE',
            credentials: 'include',
        }
    );

    if (!response.ok) {
        let message = '세부목표를 삭제하지 못했습니다.';

        try {
            const data = await response.json();
            message = data.message || message;
        } catch {}

        throw new Error(message);
    }
    return await response.json();
}