import React from "react";
import { fireEvent, render, waitFor, waitForElementToBeRemoved } from "@testing-library/react";
import Header from "./Header";
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
				<Header />
			</BrowserRouter>
		</Provider>
	);
};

describe("Header", () => {
	describe("Layout", () => {
		it("에어비앤비 이미지", () => {
			const { container } = setup();
			const icon = container.querySelector(".fab.fa-airbnb");
			expect(icon).toBeInTheDocument();
		});
		it("에어비앤비 이미지 href='/'", () => {
			const { container } = setup();
			const icon = container.querySelector(".fab.fa-airbnb");
			expect(icon.parentElement.getAttribute("href")).toBe("/");
		});
		it("검색 창 표시", () => {
			const { queryByPlaceholderText } = setup();
			const search = queryByPlaceholderText("검색 시작하기");
			expect(search).toBeInTheDocument();
		});
		it("로그아웃 상태일 때, 기본 이미지 표시", () => {
			const { container } = setup();
			const profile = container.querySelector(".profile_img");
			expect(profile).toBeInTheDocument();
		});
		it("프로필사진 없을 때, 기본 이미지 표시", () => {
			const { container } = setup(loggedInState);
			const profile = container.querySelector(".profile_img");
			expect(profile).toBeInTheDocument();
		});
		it("프로필사진 있을 때, 프로필 이미지 표시", () => {
			const { getByAltText } = setup({ ...loggedInState, profile: "profile.png" });
			const profile = getByAltText("프로필이미지");
			expect(profile).toBeInTheDocument();
		});
		it("프로필 메뉴 클릭시, 서브메뉴 표시", () => {
			const { container, queryByText } = setup();
			const profileMenu = container.querySelector(".header_profile");

			fireEvent.click(profileMenu);
			expect(queryByText("로그인")).toBeInTheDocument();
		});
	});

	describe("Interactions", () => {
		it("로그인 링크 클릭시, 로그인 창 표시", () => {
			const { container, queryByText } = setup();
			const profileMenu = container.querySelector(".header_profile");
			fireEvent.click(profileMenu);

			const loginLink = queryByText("로그인");
			fireEvent.click(loginLink);

			const loginBtn = container.querySelector("button");
			expect(loginBtn).toBeInTheDocument();
		});
		it("회원가입 링크 클릭시, 회원가입 창 표시", () => {
			const { container, queryByText } = setup();
			const profileMenu = container.querySelector(".header_profile");
			fireEvent.click(profileMenu);

			const signupLink = queryByText("회원가입");
			fireEvent.click(signupLink);

			const signupBtn = container.querySelector("button");
			expect(signupBtn).toBeInTheDocument();
		});
		it("로그인 창에서 닫기 클릭시, 사라짐", () => {
			const { container, queryByText } = setup();
			const profileMenu = container.querySelector(".header_profile");
			fireEvent.click(profileMenu);

			const loginLink = queryByText("로그인");
			fireEvent.click(loginLink);
			const exit = queryByText("❌");
			fireEvent.click(exit);

			expect(exit).not.toBeInTheDocument();
		});
		it("회원가입 창에서 닫기 클릭시, 사라짐", () => {
			const { container, queryByText } = setup();
			const profileMenu = container.querySelector(".header_profile");
			fireEvent.click(profileMenu);

			const signupLink = queryByText("회원가입");
			fireEvent.click(signupLink);
			const exit = queryByText("❌");
			fireEvent.click(exit);

			expect(exit).not.toBeInTheDocument();
		});
		it("로그인 창에서 회원가입 버튼 클릭시, 로그인 창 대신 회원가입 창 표시", () => {
			const { container, queryByText } = setup();
			const profileMenu = container.querySelector(".header_profile");
			fireEvent.click(profileMenu);

			const loginLink = queryByText("로그인");
			fireEvent.click(loginLink);
			const signupLink = container.querySelector(".signup_link");
			fireEvent.click(signupLink);

			expect(queryByText("이미 에어비앤비 계정이 있나요?")).toBeInTheDocument();
		});
		it("회원가입 창에서 로그인 버튼 클릭시, 회원가입 창 대신 로그인 창 표시", () => {
			const { container, queryByText } = setup();
			const profileMenu = container.querySelector(".header_profile");
			fireEvent.click(profileMenu);

			const signupLink = queryByText("회원가입");
			fireEvent.click(signupLink);
			const loginLink = container.querySelector(".login_link");
			fireEvent.click(loginLink);

			expect(queryByText("에어비앤비 계정이 없으세요?")).toBeInTheDocument();
		});
	});
});
