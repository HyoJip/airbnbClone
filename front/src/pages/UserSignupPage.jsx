import React, { useState } from "react";
import { connect } from "react-redux";
import styles from "../scss/pages/UserSignupPage.module.scss";
import Input from "../components/shared/Input";
import Button from "../components/shared/Button";
import classNames from "classnames/bind";

const cx = classNames.bind(styles);

const UserSignupPage = props => {
	const initialValue = {
		email: "",
		name: "",
		password: "",
		passwordRepeat: "",
	};

	const [input, setInput] = useState(initialValue);
	const [pendingApiCall, setPendingApiCall] = useState(false);
	const [errors, setErrors] = useState([]);

	const onChageInput = event => {
		const { value, name } = event.target;
		setInput(prev => ({
			...prev,
			[name]: value,
		}));
		delete errors[name];
	};

	const renderLoginPage = () => {
		props.onClickExitBtn();
		props.onClickLoginLink();
	};

	const onClickSignupBtn = () => {
		setPendingApiCall(true);
		const user = {
			email: input.email,
			name: input.name,
			password: input.password,
		};
		props.actions
			.postSignup(user)
			.then(response => {
				setPendingApiCall(false);
				renderLoginPage();
			})
			.catch(({ response }) => {
				setPendingApiCall(false);
				if (response && response.data.validationErrors) {
					setErrors(response.data.validationErrors);
				}
			});
	};

	let passwordRepeatError;
	const { password, passwordRepeat } = input;
	if (password || passwordRepeat) {
		passwordRepeatError = password === passwordRepeat ? "" : "비밀번호가 일치하지 않습니다";
	}

	return (
		<div className={cx("wrap")}>
			<div className={cx("main")}>
				<header className={cx("header")}>
					<h1 className={cx("title")}>회원가입</h1>
					<small onClick={props.onClickExitBtn}>❌</small>
				</header>
				<Input
					type="email"
					name="email"
					placeholder="이메일"
					value={input.email}
					onChange={onChageInput}
					hasError={errors.email && true}
					error={errors.email}
				/>
				<Input
					type="text"
					name="name"
					placeholder="이름"
					value={input.name}
					onChange={onChageInput}
					hasError={errors.name && true}
					error={errors.name}
				/>
				<Input
					type="password"
					name="password"
					placeholder="비밀번호"
					value={input.password}
					onChange={onChageInput}
					hasError={errors.password && true}
					error={errors.password}
				/>
				<Input
					type="password"
					name="passwordRepeat"
					placeholder="비밀번호 확인"
					value={input.passwordRepeat}
					onChange={onChageInput}
					hasError={passwordRepeatError && true}
					error={passwordRepeatError}
				/>
				<Button
					onClick={onClickSignupBtn}
					disabled={pendingApiCall || passwordRepeatError}
					pendingApiCall={pendingApiCall}
					text="가입하기"
				/>
				<p className={cx("login_wrap")}>
					이미 에어비앤비 계정이 있나요?
					<span className={cx("login_link")} onClick={renderLoginPage}>
						로그인
					</span>
				</p>
			</div>
		</div>
	);
};

UserSignupPage.defaultProps = {
	actions: {
		postSignup: () => new Promise((resolve, reject) => resolve({})),
	},
};

export default UserSignupPage;
