import React, { useState } from "react";
import Button from "../components/shared/Button";
import Input from "../components/shared/Input";
import classNames from "classnames/bind";
import styles from "../scss/pages/LoginPage.module.scss";

const cx = classNames.bind(styles);

const LoginPage = props => {
	const [input, setInput] = useState({
		email: "",
		password: "",
	});
	const [apiError, setApiError] = useState();
	const [pendingApiCall, setPendingApiCall] = useState(false);

	const onChangeInput = event => {
		const { name, value } = event.target;
		setInput(prev => ({ ...prev, [name]: value }));
		if (apiError) {
			setApiError();
		}
	};

	const renderSignupPage = () => {
		props.onClickExitBtn();
		props.onClickSignupLink();
	};

	const onClickBtn = () => {
		setPendingApiCall(true);
		const auth = {
			username: input.email,
			password: input.password,
		};
		props.actions
			.postLogin(auth)
			.then(response => {
				setPendingApiCall(false);
				props.history.push("/");
			})
			.catch(({ response }) => {
				if (response) {
					setApiError(response.data.message);
				}
				setPendingApiCall(false);
			});
	};

	const btnDisabled = !(input.email && input.password);

	return (
		<div className={cx("wrap")}>
			<div className={cx("main")}>
				<header className={cx("header")}>
					<h1 className={cx("title")}>로그인</h1>
					<small onClick={props.onClickExitBtn}>❌</small>
				</header>
				<Input
					name="email"
					type="email"
					placeholder="이메일"
					value={input.email}
					onChange={onChangeInput}
				/>
				<Input
					name="password"
					type="password"
					placeholder="비밀번호"
					value={input.password}
					onChange={onChangeInput}
				/>
				{apiError && <p className={cx("error_message")}>로그인 실패</p>}
				<Button
					onClick={onClickBtn}
					disabled={btnDisabled || pendingApiCall}
					text="로그인"
					pendingApiCall={pendingApiCall}
				/>
				<p>
					에어비앤비 계정이 없으세요?
					<span className={cx("signup_link")} onClick={renderSignupPage}>
						회원가입
					</span>
				</p>
			</div>
		</div>
	);
};

LoginPage.defaultProps = {
	actions: {
		postLogin: () => new Promise((resolve, reject) => reject({})),
	},
};

export default LoginPage;
