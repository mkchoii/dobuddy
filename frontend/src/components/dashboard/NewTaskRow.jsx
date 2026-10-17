import { useState } from 'react';

import checkboxEmptyIcon from '../../assets/icons/checkbox-empty.svg';
import './TaskRow.css';

function NewTaskRow({ onCreate, onCancel }) {
    const [content, setContent] = useState('');

    const handleSubmit = () => {
        const trimmedContent = content.trim();

        if (!trimmedContent) {
            onCancel();
            return;
        }

        onCreate(trimmedContent);
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            e.currentTarget.blur();
        }
    };

    return (
        <div className="task-row new-task-row">
            <div className="task-checkbox-button">
                <img
                    src={checkboxEmptyIcon}
                    alt=""
                    className="task-checkbox-icon"
                />
            </div>

            <div className="new-task-input-area">
                <input
                    type="text"
                    className="new-task-input"
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    onKeyDown={handleKeyDown}
                    onBlur={handleSubmit}
                    placeholder="태스크를 입력해주세요 (20자 이내)"
                    maxLength={20}
                    enterKeyHint='done'
                    autoFocus
                />
            </div>
            
        </div>
    );
}

export default NewTaskRow;