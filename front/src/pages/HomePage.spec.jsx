import React from "react";
import { render } from "@testing-library/react";
import HomePage from "./HomePage";

describe("HomePage", () => {
	describe("Layout", () => {
		it("홈페이지", () => {
			const { queryByTestId } = render(<HomePage />);
			const homePageDiv = queryByTestId("homepage");
			expect(homePageDiv).toBeInTheDocument();
		});
	});
});
