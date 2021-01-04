import React from "react";
import { fireEvent, render, waitFor } from "@testing-library/react";
import LoginPage from "./LoginPage";

describe("LoginPage", () => {
	describe("Layout", () => {
		it("제목 표시", () => {
			const { container } = render(<LoginPage />);
			const h1 = container.querySelector("h1");
			expect(h1).toBeInTheDocument();
		});
		it("이메일 인풋 표시", () => {
			const { queryByPlaceholderText } = render(<LoginPage />);
			const emailInput = queryByPlaceholderText("이메일");
			expect(emailInput).toBeInTheDocument();
		});
		it("비밀번호 인풋 표시", () => {
			const { queryByPlaceholderText } = render(<LoginPage />);
			const passwordInput = queryByPlaceholderText("비밀번호");
			expect(passwordInput).toBeInTheDocument();
		});
		it("비밀번호 인풋 비밀번호 타입", () => {
			const { queryByPlaceholderText } = render(<LoginPage />);
			const passwordInput = queryByPlaceholderText("비밀번호");
			expect(passwordInput.type).toBe("password");
		});
		it("로그인 버튼 표시", () => {
			const { container } = render(<LoginPage />);
			const loginBtn = container.querySelector("button");
			expect(loginBtn).toBeInTheDocument();
		});
	});

	describe("Interactions", () => {
		const chageEvent = text => ({ target: { value: text } });
		const mockAsyncDelayed = () =>
			jest
				.fn()
				.mockImplementation(
					() => new Promise((resolve, reject) => setTimeout(() => resolve({}), 400))
				);

		let emailInput, passwordInput, button;
		const setupForSubmit = props => {
			const rendered = render(<LoginPage {...props} />);
			emailInput = rendered.queryByPlaceholderText("이메일");
			passwordInput = rendered.queryByPlaceholderText("비밀번호");
			fireEvent.change(emailInput, chageEvent("test@google.com"));
			fireEvent.change(passwordInput, chageEvent("P4ssword"));

			button = rendered.container.querySelector("button");
			return rendered;
		};
		it("이메일 인풋 value state 동기화", () => {
			const { queryByPlaceholderText } = render(<LoginPage />);
			const emailInput = queryByPlaceholderText("이메일");

			fireEvent.change(emailInput, chageEvent("test@google.com"));
			expect(emailInput).toHaveValue("test@google.com");
		});
		it("패스워드 인풋 value state 동기화", () => {
			const { queryByPlaceholderText } = render(<LoginPage />);
			const passwordInput = queryByPlaceholderText("비밀번호");

			fireEvent.change(passwordInput, chageEvent("P4ssword"));
			expect(passwordInput).toHaveValue("P4ssword");
		});
		it("로그인 버튼 클릭시, login 함수 실행", () => {
			const actions = {
				postLogin: jest.fn().mockResolvedValue({}),
			};
			setupForSubmit({ actions });
			fireEvent.click(button);

			expect(actions.postLogin).toHaveBeenCalledTimes(1);
		});
		it("actions가 없을 때 로그인 버튼 클릭시, 에러 발생 X", () => {
			setupForSubmit();

			expect(() => fireEvent.click(button)).not.toThrow();
		});
		it("로그인 버튼 클릭시, 데이터 전송", () => {
			const actions = {
				postLogin: jest.fn().mockResolvedValue({}),
			};
			setupForSubmit({ actions });
			fireEvent.click(button);

			const expectedUserAuthObject = {
				username: "test@google.com",
				password: "P4ssword",
			};

			expect(actions.postLogin).toHaveBeenCalledWith(expectedUserAuthObject);
		});
		it("이메일 비밀번호 입력시 버튼 활성화", () => {
			setupForSubmit();
			expect(button).toBeEnabled();
		});
		it("이메일 미입력시, 버튼 비활성화", () => {
			setupForSubmit();
			fireEvent.change(emailInput, chageEvent(""));
			expect(button).toBeDisabled();
		});
		it("로그인 실패시, 에러메세지 표시", async () => {
			const actions = {
				postLogin: jest.fn().mockRejectedValue({
					response: {
						data: {
							message: "로그인 실패",
						},
					},
				}),
			};
			const { findByText } = setupForSubmit({ actions });

			fireEvent.click(button);
			const alert = await findByText("로그인 실패");

			expect(alert).toBeInTheDocument();
		});
		it("로그인 실패 후 인풋 값 변경시, 에러메세지 사라짐", async () => {
			const actions = {
				postLogin: jest.fn().mockRejectedValue({
					response: {
						data: {
							message: "로그인 실패",
						},
					},
				}),
			};
			const { findByText } = setupForSubmit({ actions });
			fireEvent.click(button);

			const alert = await findByText("로그인 실패");
			fireEvent.change(passwordInput, chageEvent("differ"));

			expect(alert).not.toBeInTheDocument();
		});
		it("버튼을 2번 누르더라도 로그인 요청은 한번만 유효", () => {
			const actions = {
				postLogin: mockAsyncDelayed(),
			};
			setupForSubmit({ actions });
			fireEvent.click(button);
			fireEvent.click(button);

			expect(actions.postLogin).toHaveBeenCalledTimes(1);
		});
		it("로그인 요청 중, 스피너 표시", () => {
			const actions = {
				postLogin: mockAsyncDelayed(),
			};
			const { container } = setupForSubmit({ actions });
			fireEvent.click(button);

			const spinner = container.querySelector(".spinner");
			expect(spinner).toBeInTheDocument();
		});
		it("로그인 처리 후, 스피너 숨기기", async () => {
			const actions = {
				postLogin: jest.fn().mockResolvedValue({}),
			};
			const { container } = setupForSubmit({ actions });
			fireEvent.click(button);

			const spinner = container.querySelector(".spinner");
			await waitFor(() => expect(spinner).not.toBeInTheDocument());
		});
		it("로그인 실패 후, 스피너 숨기기", async () => {
			const actions = {
				postLogin: jest.fn().mockRejectedValue({}),
			};
			const { container } = setupForSubmit({ actions });
			fireEvent.click(button);

			const spinner = container.querySelector(".spinner");
			await waitFor(() => expect(spinner).not.toBeInTheDocument());
		});
	});
});

console.error = () => {};
