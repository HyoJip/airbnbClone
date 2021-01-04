import React from "react";
import { fireEvent, render, waitFor } from "@testing-library/react";
import UserSignupPage from "./UserSignupPage";

describe("UserSignupPage", () => {
	describe("Layout", () => {
		it("회원가입 제목 표시", () => {
			const { container } = render(<UserSignupPage />);
			const h1 = container.querySelector("h1");
			expect(h1).toBeInTheDocument();
		});
		it("이메일 인풋 표시", () => {
			const { queryByPlaceholderText } = render(<UserSignupPage />);
			const email = queryByPlaceholderText("이메일");
			expect(email).toBeInTheDocument();
		});
		it("이메일 인풋 이메일 타입", () => {
			const { queryByPlaceholderText } = render(<UserSignupPage />);
			const email = queryByPlaceholderText("이메일");
			expect(email.type).toBe("email");
		});
		it("이름 인풋 표시", () => {
			const { queryByPlaceholderText } = render(<UserSignupPage />);
			const name = queryByPlaceholderText("이름");
			expect(name).toBeInTheDocument();
		});
		it("비밀번호 인풋 표시", () => {
			const { queryByPlaceholderText } = render(<UserSignupPage />);
			const password = queryByPlaceholderText("비밀번호");
			expect(password).toBeInTheDocument();
		});
		it("비밀번호 인풋 비밀번호 타입", () => {
			const { queryByPlaceholderText } = render(<UserSignupPage />);
			const password = queryByPlaceholderText("비밀번호");
			expect(password.type).toBe("password");
		});
		it("비밀번호 확인 인풋 표시", () => {
			const { queryByPlaceholderText } = render(<UserSignupPage />);
			const passwordRepeat = queryByPlaceholderText("비밀번호 확인");
			expect(passwordRepeat.type).toBe("password");
		});
		it("비밀번호 확인 인풋 표시", () => {
			const { container } = render(<UserSignupPage />);
			const button = container.querySelector("button");
			expect(button).toBeInTheDocument();
		});
	});

	describe("Interactions", () => {
		const chageEvent = text => ({ target: { value: text } });

		let email, password, passwordRepeat, name, button;

		const setupForSubmit = props => {
			const rendered = render(<UserSignupPage {...props} />);
			email = rendered.queryByPlaceholderText("이메일");
			name = rendered.queryByPlaceholderText("이름");
			password = rendered.queryByPlaceholderText("비밀번호");
			passwordRepeat = rendered.queryByPlaceholderText("비밀번호 확인");
			button = rendered.container.querySelector("button");

			fireEvent.change(email, chageEvent("my-email@naver.com"));
			fireEvent.change(name, chageEvent("홍길동"));
			fireEvent.change(password, chageEvent("P4ssword"));
			fireEvent.change(passwordRepeat, chageEvent("P4ssword"));
			return rendered;
		};

		const mockAsyncDelayed = () =>
			jest
				.fn()
				.mockImplementation(
					() => new Promise((resolve, reject) => setTimeout(() => resolve({}), 400))
				);

		it("이메일 입력", () => {
			const { queryByPlaceholderText } = render(<UserSignupPage />);
			const email = queryByPlaceholderText("이메일");

			fireEvent.change(email, chageEvent("my-email@naver.com"));

			expect(email).toHaveValue("my-email@naver.com");
		});
		it("이름 입력", () => {
			const { queryByPlaceholderText } = render(<UserSignupPage />);
			const name = queryByPlaceholderText("이름");

			fireEvent.change(name, chageEvent("홍길동"));

			expect(name).toHaveValue("홍길동");
		});
		it("비밀번호 입력", () => {
			const { queryByPlaceholderText } = render(<UserSignupPage />);
			const password = queryByPlaceholderText("비밀번호");

			fireEvent.change(password, chageEvent("P4ssword"));

			expect(password).toHaveValue("P4ssword");
		});
		it("비밀번호 확인 입력", () => {
			const { queryByPlaceholderText } = render(<UserSignupPage />);
			const passwordRepeat = queryByPlaceholderText("비밀번호 확인");

			fireEvent.change(passwordRepeat, chageEvent("P4ssword"));

			expect(passwordRepeat).toHaveValue("P4ssword");
		});
		it("회원가입 버튼 클릭시 회원가입 함수 호출", () => {
			const actions = {
				postSignup: jest.fn().mockResolvedValue({}),
			};

			setupForSubmit({ actions });
			fireEvent.click(button);

			expect(actions.postSignup).toHaveBeenCalledTimes(1);
		});
		it("actions 없을 경우, 회원가입 버튼 클릭 시 에러페이지 표시 X", () => {
			setupForSubmit();

			expect(() => fireEvent.click(button)).not.toThrow();
		});
		it("인풋들이 유효할 경우, 회원가입 버튼 클릭시 데이터 전송", () => {
			const actions = {
				postSignup: jest.fn().mockResolvedValue({}),
			};
			setupForSubmit({ actions });
			fireEvent.click(button);

			const expectedUserObject = {
				email: "my-email@naver.com",
				name: "홍길동",
				password: "P4ssword",
			};
			expect(actions.postSignup).toHaveBeenCalledWith(expectedUserObject);
		});
		it("버튼을 2번 누르더라도 회원가입 요청은 한번만 유효", () => {
			const actions = {
				postSignup: mockAsyncDelayed(),
			};
			setupForSubmit({ actions });
			fireEvent.click(button);
			fireEvent.click(button);

			expect(actions.postSignup).toHaveBeenCalledTimes(1);
		});
		it("회원가입 요청 중, 스피너 표시", () => {
			const actions = {
				postSignup: mockAsyncDelayed(),
			};
			const { container } = setupForSubmit({ actions });
			fireEvent.click(button);

			const spinner = container.querySelector(".spinner");
			expect(spinner).toBeInTheDocument();
		});
		it("회원가입 처리 후, 스피너 숨기기", async () => {
			const actions = {
				postSignup: jest.fn().mockResolvedValue({}),
			};
			const { container } = setupForSubmit({ actions });
			fireEvent.click(button);

			const spinner = container.querySelector(".spinner");
			await waitFor(() => expect(spinner).not.toBeInTheDocument());
		});
		it("회원가입 실패 후, 스피너 숨기기", async () => {
			const actions = {
				postSignup: jest.fn().mockRejectedValue({}),
			};
			const { container } = setupForSubmit({ actions });
			fireEvent.click(button);

			const spinner = container.querySelector(".spinner");
			await waitFor(() => expect(spinner).not.toBeInTheDocument());
		});
		it("회원가입 실패 후, 에러 메시지 표시", async () => {
			const actions = {
				postSignup: jest.fn().mockRejectedValue({
					response: {
						data: {
							validationErrors: {
								email: "잘못된 이메일 형식입니다",
							},
						},
					},
				}),
			};
			const { findByText } = setupForSubmit({ actions });
			fireEvent.click(button);

			const message = await findByText("잘못된 이메일 형식입니다");
			expect(message).toBeInTheDocument();
		});
		it("비밀번호와 비밀번호 확인 일치시, 버튼 활성화", () => {
			setupForSubmit();
			expect(button).not.toBeDisabled();
		});
		it("비밀번호와 비밀번호 확인 비일치시, 버튼 비활성화", () => {
			setupForSubmit();
			fireEvent.change(passwordRepeat, chageEvent("differ"));
			expect(button).toBeDisabled();
		});
		it("비밀번호와 비밀번호 확인 비일치시, 버튼 비활성화", () => {
			setupForSubmit();
			fireEvent.change(password, chageEvent("differ"));
			expect(button).toBeDisabled();
		});
		it("비밀번호와 비밀번호 확인 비일치시, 에러메시지 표시", () => {
			const { queryByText } = setupForSubmit();
			fireEvent.change(password, chageEvent("differ"));

			const message = queryByText("비밀번호가 일치하지 않습니다");
			expect(message).toBeInTheDocument();
		});
		it("비밀번호와 비밀번호 확인 비일치시, 에러메시지 표시", () => {
			const { queryByText } = setupForSubmit();
			fireEvent.change(passwordRepeat, chageEvent("differ"));

			const message = queryByText("비밀번호가 일치하지 않습니다");
			expect(message).toBeInTheDocument();
		});
		it("회원가입 실패 후, 에러 메시지 표시", async () => {
			const actions = {
				postSignup: jest.fn().mockRejectedValue({
					response: {
						data: {
							validationErrors: {
								email: "잘못된 이메일 형식입니다",
							},
						},
					},
				}),
			};
			const { findByText } = setupForSubmit({ actions });
			fireEvent.click(button);
			const message = await findByText("잘못된 이메일 형식입니다");

			fireEvent.change(email, chageEvent("differ"));
			expect(message).not.toBeInTheDocument();
		});
	});
});

console.error = () => {};
