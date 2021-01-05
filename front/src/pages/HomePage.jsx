import React from "react";
import classNames from "classnames/bind";
import styles from "../scss/pages/HomePage.module.scss";

const cx = classNames.bind(styles);

const HomePage = () => {
	return (
		<div className={cx("wrap")} data-testid="homepage">
			<div className={cx("main")}>
				<h1 className={cx("title")}>이제, 여행은 가까운 곳에서.</h1>
				<p className={cx("description")}>
					새로운 곳에서 머물러보세요. 직접 살아보거나, 업무를 보거나, 휴식할 수 있는
					가까운 숙소를 찾아보세요.
				</p>
				<a className={cx("room_link")} href="/room">
					여행지 둘러보기
				</a>
			</div>
		</div>
	);
};

export default HomePage;
