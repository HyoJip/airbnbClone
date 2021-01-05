import React from "react";
import { render } from "@testing-library/react";
import App from "./App";
import { MemoryRouter } from "react-router-dom";
import { Provider } from "react-redux";
import authReducer from "../redux/authReducer";
import { createStore } from "redux";

const store = createStore(authReducer);
const setup = path => {
	return render(
		<Provider store={store}>
			<MemoryRouter initialEntries={[path]}>
				<App />
			</MemoryRouter>
		</Provider>
	);
};

describe("App", () => {
	it("/ -> 홈페이지", () => {
		const { queryByTestId } = setup("/");
		expect(queryByTestId("homepage")).toBeInTheDocument();
	});
});
