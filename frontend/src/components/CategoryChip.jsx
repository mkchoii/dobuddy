import './CategoryChip.css';

function CategoryChip({
    category,
    label,
    isSelected = false,
    isLinked = false,
    onClick,
    size = 'default',
}) {
    const className = `category-chip category-${category.value.toLowerCase()} ${
        isSelected ? 'selected' : ''
    } ${size === 'small' ? 'small' : ''}`;

    const content = (
        <>
            <span>{label ?? category.label}</span>
            {isLinked && (
                <span
                className="category-chip-link-icon"
                aria-label="스터디 연동됨"
                role="img"
                />
            )}
        </>
    );

    // 클릭 기능이 있는 경우
    if (onClick) {
        return (
            <button
                type="button"
                className={className}
                onClick={onClick}
            >
                {content}
            </button>
        );
    }

    // 클릭 기능이 없는 경우
    return (
        <span className={className}>
            {content}
        </span>
    );
}

export default CategoryChip;