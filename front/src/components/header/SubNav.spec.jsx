import React from "react";
import { fireEvent, render } from "@testing-library/react";
import SubNav from "./SubNav";
import { Provider } from "react-redux";
import { createStore } from "redux";
import authReducer from "../../redux/authReducer";
import { BrowserRouter } from "react-router-dom";

const loggedInState = {
	id: 1,
	email: "test@naver.com",
	name: "홍길동",
	password: "P4ssword",
	profile: "",
	status: "GUEST",
	isLoggedIn: true,
};
const defaultState = {
	id: 0,
	email: "",
	name: "",
	password: "",
	profile: "",
	status: "",
	isLoggedIn: false,
};

const setup = (initialState = defaultState) => {
	const store = createStore(authReducer, initialState);
	return render(
		<Provider store={store}>
			<BrowserRouter>
				<SubNav />
			</BrowserRouter>
		</Provider>
	);
};

describe("SubNav", () => {
	describe("Layout", () => {
		it("SubNav 컴포넌트 표시", () => {
			const { container } = setup();
			const ul = container.querySelector("ul");
			expect(ul).toBeInTheDocument();
		});
		it("로그아웃 상태에서 로그인 버튼 표시", () => {
			const { queryByText } = setup();
			const login = queryByText("로그인");
			expect(login).toBeInTheDocument();
		});
		it("로그아웃 상태에서 회원가입 버튼 표시", () => {
			const { queryByText } = setup();
			const signup = queryByText("회원가입");
			expect(signup).toBeInTheDocument();
		});
		it("로그인 상태에서 로그아웃 버튼 표시", () => {
			const { queryByText } = setup(loggedInState);
			const logout = queryByText("로그아웃");
			expect(logout).toBeInTheDocument();
		});
		it("로그인 상태에서 회원정보 수정 버튼 표시", () => {
			const { queryByText } = setup(loggedInState);
			const userUpdateBtn = queryByText("회원 정보 수정");
			expect(userUpdateBtn).toBeInTheDocument();
		});
		it("로그인한 회원이 게스트일 경우 게스트 버튼 표시", () => {
			const { queryByText } = setup(loggedInState);
			const becomeHostBtn = queryByText("숙소 호스트 되기");
			expect(becomeHostBtn).toBeInTheDocument();
		});
		it("로그인한 회원이 호스트일 경우 호스트 버튼 표시", () => {
			const { queryByText } = setup({ ...loggedInState, status: "HOST" });
			const myRoom = queryByText("내 숙소");
			expect(myRoom).toBeInTheDocument();
		});
	});
});
