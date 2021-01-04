import React, { useState } from "react";
import Button from "../components/shared/Button";
import Input from "../components/shared/Input";

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

	const onClickBtn = () => {
		setPendingApiCall(true);
		const auth = {
			username: input.email,
			password: input.password,
		};
		props.actions
			.postLogin(auth)
			.then(response => setPendingApiCall(false))
			.catch(({ response }) => {
				if (response) {
					setApiError(response.data.message);
				}
				setPendingApiCall(false);
			});
	};

	const btnDisabled = !(input.email && input.password);

	return (
		<div>
			<h1>로그인</h1>
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
			{apiError && <p className="error_message">로그인 실패</p>}
			<Button
				onClick={onClickBtn}
				disabled={btnDisabled || pendingApiCall}
				text="로그인"
				pendingApiCall={pendingApiCall}
			/>
			<p>
				에어비앤비 계정이 없으세요? <a href="">회원 가입</a>
			</p>
		</div>
	);
};

LoginPage.defaultProps = {
	actions: {
		postLogin: () => new Promise((resolve, reject) => reject({})),
	},
};

export default LoginPage;
