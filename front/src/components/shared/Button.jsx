import React from "react";

const Button = props => {
	return (
		<button onClick={props.onClick} disabled={props.disabled}>
			{props.pendingApiCall && <i className="spinner"></i>}
			{props.text}
		</button>
	);
};

export default Button;
