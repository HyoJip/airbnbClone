import React from "react";
import styles from "../../scss/components/Input.module.scss";
import classNames from "classnames/bind";

const cx = classNames.bind(styles);

const Input = props => {
	return (
		<div>
			{props.label && <label htmlFor={props.name}>{props.label}</label>}
			<input
				name={props.name}
				className={
					props.hasError === undefined
						? cx("field")
						: cx("field", { invalid: props.hasError, valid: !props.hasError })
				}
				type={props.type || "text"}
				placeholder={props.placeholder}
				value={props.value}
				onChange={props.onChange}
			/>
			{props.hasError && <p className={cx("error_message")}>{props.error}</p>}
		</div>
	);
};

Input.defaultProps = {
	onChange: () => {},
};

export default Input;
