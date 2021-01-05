import React from "react";
import styles from "../../scss/components/Button.module.scss";
import classNames from "classnames/bind";

const cx = classNames.bind(styles);

const Button = props => {
	return (
		<button onClick={props.onClick} disabled={props.disabled}>
			{props.pendingApiCall && <i className={cx("spinner")}></i>}
			{props.text}
		</button>
	);
};

export default Button;
