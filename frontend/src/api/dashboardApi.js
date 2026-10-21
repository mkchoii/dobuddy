import { apiFetch } from "./apiFetch";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export async function getDashboard(date) {
    const url = date
        ? `${API_BASE_URL}/dashboard?date=${date}`
        : `${API_BASE_URL}/dashboard`;

    const response = await apiFetch(url, {
        method: 'GET',
    });

    if (!response.ok) {
        let message = '대시보드를 불러오지 못했습니다.';

        try {
            const data = await response.json();
            message = data.message || message;
        } catch {
            // 응답 body가 없는 경우
        }

        throw new Error(message);
    }

    const data = await response.json();
    return {
        ...data,
        goals: data.goals.map((goal) => ({
            ...goal,
            tasks: goal.tasks.map((task) => ({
                ...task,
                isCompleted: task.completed ?? task.isCompleted ?? false,
            })),
        })),
    };
}

export async function updateTaskCompletion(taskId, isCompleted) {
    const response = await apiFetch(
        `${API_BASE_URL}/tasks/${taskId}/completion`,
        {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                isCompleted,
            }),
        }
    );

    if (!response.ok) {
        let message = '태스크 상태를 변경하지 못했습니다.';

        try {
            const data = await response.json();
            message = data.message || message;
        } catch {
            // response body가 없는 경우
        }

        throw new Error(message);
    }

    const data = await response.json();
    return {
        ...data,
        isCompleted: data.completed ?? data.isCompleted ?? isCompleted,
    };
}

export async function updateTask(taskId, content) {
    const response = await apiFetch(
        `${API_BASE_URL}/tasks/${taskId}`,
        {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                content,
            }),
        }
    );

    if (!response.ok) {
        let message = '태스크를 수정하지 못했습니다.';

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

export async function deleteTask(taskId) {
    const response = await apiFetch(
        `${API_BASE_URL}/tasks/${taskId}`,
        {
            method: 'DELETE',
        }
    );

    if (!response.ok) {
        let message = '태스크를 삭제하지 못했습니다.';

        try {
            const data = await response.json();
            message = data.message || message;
        } catch {
            // response body가 없는 경우
        }

        throw new Error(message);
    }

    return response.json();
}

export async function createTask(goalId, taskDate, content) {
    const response = await apiFetch(
        `${API_BASE_URL}/tasks`,
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                goalId,
                taskDate,
                content,
            }),
        }
    );

    if (!response.ok) {
        let message = '태스크를 생성하지 못했습니다.';

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