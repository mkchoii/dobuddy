import chatPromoImage from '../../assets/chat-promo.png';
import closeIcon from '../../assets/icons/close.svg';
import './ChatPromoModal.css';

function ChatPromoModal({ onClose }) {
  return (
    <div className="chat-promo-overlay">
      <div className="chat-promo-modal">
        <img 
            src={chatPromoImage}
            alt="오픈채팅방 홍보"
            className="chat-promo-image"
        />

        <button
            type="button"
            className="chat-promo-close"
            onClick={onClose}
            aria-label="팝업 닫기"
        >
            <img
                src={closeIcon}
                alt=""
                className="chat-promo-close-icon"
            />
        </button>

      </div>
    </div>
  );
}

export default ChatPromoModal;