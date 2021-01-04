import React from "react";
import { fireEvent, render } from "@testing-library/react";
import Input from "./Input";

describe("Input", () => {
	describe("Layout", () => {
		it("Input 컴포넌트 표시", () => {
			const { container } = render(<Input />);
			const input = container.querySelector("input");
			expect(input).toBeInTheDocument();
		});
		it("label 표시", () => {
			const { queryByText } = render(<Input label="test-label" />);
			const label = queryByText("test-label");
			expect(label).toBeInTheDocument();
		});
		it("props.label 없을 경우, 표시 X", () => {
			const { queryByText } = render(<Input />);
			const label = queryByText("test-label");
			expect(label).not.toBeInTheDocument();
		});
		it("props.type 없을 경우, 기본 text 타입", () => {
			const { container } = render(<Input />);
			const input = container.querySelector("input");
			expect(input.type).toBe("text");
		});
		it("type 속성 적용", () => {
			const { container } = render(<Input type="password" />);
			const input = container.querySelector("input");
			expect(input.type).toBe("password");
		});
		it("placeholder 표시", () => {
			const { container } = render(<Input placeholder="test-placeholder" />);
			const input = container.querySelector("input");
			expect(input.placeholder).toBe("test-placeholder");
		});
		it("value 속성 적용", () => {
			const { container } = render(<Input value="test-value" />);
			const input = container.querySelector("input");
			expect(input).toHaveValue("test-value");
		});
		it("onChange 속성 적용", () => {
			const onChange = jest.fn();
			const { container } = render(<Input onChange={onChange} />);
			const input = container.querySelector("input");

			fireEvent.change(input, { target: { value: "new-value" } });

			expect(onChange).toHaveBeenCalledTimes(1);
		});
		it("에러가 없을 때, 기본 스타일", () => {
			const { container } = render(<Input />);
			const input = container.querySelector("input");
			expect(input.className).toBe("field");
		});
		it("hasError 속성이 false일 때, className valid 추가", () => {
			const { container } = render(<Input hasError={false} />);
			const input = container.querySelector("input");
			expect(input.className).toBe("field valid");
		});
		it("hasError 속성이 true일 때, className invalid ", () => {
			const { container } = render(<Input hasError />);
			const input = container.querySelector("input");
			expect(input.className).toBe("field invalid");
		});
		it("에러메시지가 있을 때, 에러 표시 ", () => {
			const { queryByText } = render(<Input hasError error="test-error" />);
			const message = queryByText("test-error");
			expect(message).toBeInTheDocument();
		});
		it("에러메시지가 있어도 에러가 false면, 에러 표시 X", () => {
			const { queryByText } = render(<Input error="test-error" />);
			const message = queryByText("test-error");
			expect(message).not.toBeInTheDocument();
		});
	});
});
