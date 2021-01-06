import React from "react";
import { fireEvent, render, waitForElementToBeRemoved } from "@testing-library/react";
import App from "./App";
import { MemoryRouter } from "react-router-dom";
import { Provider } from "react-redux";
import axios from "axios";
import configureStore from "../redux/configureStore";

beforeEach(() => {
	localStorage.clear();
	delete axios.defaults.headers.common["Authorization"];
});

const setup = path => {
	return render(
		<Provider store={configureStore(false)}>
			<MemoryRouter initialEntries={[path]}>
				<App />
			</MemoryRouter>
		</Provider>
	);
};
const chageEvent = text => ({ target: { value: text } });

const setUserInLocalStorage = () => {
	localStorage.setItem(
		"USER_AUTH",
		JSON.stringify({
			id: 1,
			email: "test@google.com",
			password: "P4ssword",
			name: "홍길동",
			profile: "",
			status: "GUEST",
			isLoggedIn: true,
		})
	);
};

const mockSucessGetUser = {
	data: {
		id: 1,
		email: "test@google.com",
		name: "홍길동",
		profile: "",
		status: "GUEST",
	},
};

describe("App", () => {
	let profileMenu, loginLink, signupLink, button;

	const setupForLogin = path => {
		const rendered = setup(path);
		profileMenu = rendered.container.querySelector(".header_profile");
		fireEvent.click(profileMenu);
		loginLink = rendered.queryByText("로그인");
		fireEvent.click(loginLink);
		const emailInput = rendered.queryByPlaceholderText("이메일");
		const passwordInput = rendered.queryByPlaceholderText("비밀번호");
		button = rendered.container.querySelector("button");
		fireEvent.change(emailInput, chageEvent("test@google.com"));
		fireEvent.change(passwordInput, chageEvent("P4ssword"));
		return rendered;
	};

	const setupForSignup = path => {
		const rendered = setup(path);
		const profileMenu = rendered.container.querySelector(".header_profile");
		fireEvent.click(profileMenu);
		signupLink = rendered.queryByText("회원가입");
		fireEvent.click(signupLink);
		const emailInput = rendered.queryByPlaceholderText("이메일");
		const nameInput = rendered.queryByPlaceholderText("이름");
		const passwordInput = rendered.queryByPlaceholderText("비밀번호");
		const passwordRepeatInput = rendered.queryByPlaceholderText("비밀번호 확인");
		button = rendered.container.querySelector("button");
		fireEvent.change(emailInput, chageEvent("my-email@naver.com"));
		fireEvent.change(nameInput, chageEvent("홍길동"));
		fireEvent.change(passwordInput, chageEvent("P4ssword"));
		fireEvent.change(passwordRepeatInput, chageEvent("P4ssword"));
		return rendered;
	};

	it("/ -> 홈페이지", () => {
		const { queryByTestId } = setup("/");
		expect(queryByTestId("homepage")).toBeInTheDocument();
	});
	it("로그인 성공시, 로그아웃 링크 표시", async () => {
		axios.post = jest.fn().mockResolvedValue(mockSucessGetUser);
		setupForLogin("/");
		fireEvent.click(button);

		await waitForElementToBeRemoved(button);
		fireEvent.click(profileMenu);
		expect(loginLink).not.toBeInTheDocument();
	});
	it("회원가입 성공시, 자동으로 로그인", async () => {
		axios.post = jest
			.fn()
			.mockResolvedValueOnce({ message: "회원가입 성공" })
			.mockResolvedValueOnce(mockSucessGetUser);
		setupForSignup("/");
		fireEvent.click(button);

		await waitForElementToBeRemoved(button);
		fireEvent.click(profileMenu);
		expect(signupLink).not.toBeInTheDocument();
	});
	it("로그인 성공시, 회원 정보 로컬스토리지에 저장", async () => {
		axios.post = jest.fn().mockResolvedValue(mockSucessGetUser);
		setupForLogin("/");
		fireEvent.click(button);

		await waitForElementToBeRemoved(button);
		const dataInStorage = JSON.parse(localStorage.getItem("USER_AUTH"));
		expect(dataInStorage).toEqual({
			...mockSucessGetUser.data,
			password: "P4ssword",
			isLoggedIn: true,
		});
	});
	it("로컬스토리지에 회원인증정보가 있을 경우, 로그인 상태 유지", () => {
		setUserInLocalStorage();
		const { container, queryByText } = setup("/");

		const profileMenu = container.querySelector(".header_profile");
		fireEvent.click(profileMenu);

		expect(queryByText("여행")).toBeInTheDocument();
	});
	it("로그인 후, http Basic 인증헤더에 인증정보 저장", async () => {
		axios.post = jest.fn().mockResolvedValue(mockSucessGetUser);
		setupForLogin("/");
		fireEvent.click(button);
		await waitForElementToBeRemoved(button);

		const axiosAuthorization = axios.defaults.headers.common["Authorization"];
		const encoded = btoa("test@google.com:P4ssword");
		const expectedAuthorization = `Basic ${encoded}`;

		expect(axiosAuthorization).toBe(expectedAuthorization);
	});
	it("로컬스토리지에 회원인증정보가 있을 경우, http Basic 인증헤더에 인증정보 저장", () => {
		setUserInLocalStorage();
		setup("/");

		const axiosAuthorization = axios.defaults.headers.common["Authorization"];
		const encoded = btoa("test@google.com:P4ssword");
		const expectedAuthorization = `Basic ${encoded}`;

		expect(axiosAuthorization).toBe(expectedAuthorization);
	});
	it("로그아웃시, 인증정보 삭제", () => {
		setUserInLocalStorage();
		const { container, queryByText } = setup("/");

		const profileMenu = container.querySelector(".header_profile");
		fireEvent.click(profileMenu);
		fireEvent.click(queryByText("로그아웃"));

		const axiosAuthorization = axios.defaults.headers.common["Authorization"];
		expect(axiosAuthorization).toBeFalsy();
	});
});
