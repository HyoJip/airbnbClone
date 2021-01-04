import React from "react";

const Input = props => {
	let inputClassName = "field";
	if (props.hasError != undefined) {
		inputClassName += props.hasError ? " invalid" : " valid";
	}

	return (
		<div>
			{props.label && <label htmlFor={props.name}>{props.label}</label>}
			<input
				name={props.name}
				className={inputClassName}
				type={props.type || "text"}
				placeholder={props.placeholder}
				value={props.value}
				onChange={props.onChange}
			/>
			{props.hasError && <p className="error_message">{props.error}</p>}
		</div>
	);
};

Input.defaultProps = {
	onChange: () => {},
};

export default Input;
