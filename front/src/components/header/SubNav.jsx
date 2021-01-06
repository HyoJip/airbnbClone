import React from "react";
import { connect } from "react-redux";
import classNames from "classnames/bind";
import styles from "../../scss/components/SubNav.module.scss";
import { Link } from "react-router-dom";
import { logoutHandler } from "../../redux/authActions";

const cx = classNames.bind(styles);

const SubNav = props => {
	let navs;
	if (props.user.isLoggedIn) {
		const guest = <li className={cx("submenu", "host_btn")}>숙소 호스트 되기</li>;
		const host = (
			<>
				<li className={cx("submenu", "host_btn")}>내 숙소</li>
				<li className={cx("submenu", "host_btn")}>내 숙소 예약 관리</li>
			</>
		);
		navs = (
			<ul className={cx("subnav")}>
				<li className={cx("submenu")}>메시지</li>
				<li className={cx("submenu")}>여행</li>
				{props.user.status === "GUEST" ? guest : host}
				<li className={cx("submenu")}>회원 정보 수정</li>
				<li className={cx("submenu")} onClick={props.logoutHandler}>
					로그아웃
				</li>
			</ul>
		);
	} else {
		navs = (
			<ul className={cx("subnav")}>
				<li className={cx("submenu")} onClick={props.onClickLoginLink}>
					로그인
				</li>
				<li className={cx("submenu")} onClick={props.onClickSignupLink}>
					회원가입
				</li>
			</ul>
		);
	}
	return navs;
};

const mapStateToProps = state => ({ user: state });

export default connect(mapStateToProps, { logoutHandler })(SubNav);
