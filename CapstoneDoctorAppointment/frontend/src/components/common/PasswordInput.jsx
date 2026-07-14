import { useState, forwardRef } from "react";
import { Form, InputGroup } from "react-bootstrap";

const EyeIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
    <path
      d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7z"
      stroke="currentColor"
      strokeWidth="2"
    />
    <circle cx="12" cy="12" r="3" stroke="currentColor" strokeWidth="2" />
  </svg>
);

const EyeOffIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
    <path
      d="M3 3l18 18M10.6 10.6a3 3 0 004.24 4.24M9.9 4.24A11 11 0 0112 4c7 0 11 7 11 7a13.2 13.2 0 01-3.1 3.9M6.6 6.6A13.1 13.1 0 001 11s4 7 11 7a10.6 10.6 0 004.9-1.2"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
    />
  </svg>
);

const PasswordInput = forwardRef(({ isInvalid, ...rest }, ref) => {
  const [visible, setVisible] = useState(false);

  return (
    <InputGroup hasValidation>
      <Form.Control
        type={visible ? "text" : "password"}
        ref={ref}
        isInvalid={isInvalid}
        {...rest}
      />
      <InputGroup.Text
        role="button"
        onClick={() => setVisible((prev) => !prev)}
        aria-label={visible ? "Hide password" : "Show password"}
      >
        {visible ? <EyeOffIcon /> : <EyeIcon />}
      </InputGroup.Text>
    </InputGroup>
  );
});

PasswordInput.displayName = "PasswordInput";

export default PasswordInput;
