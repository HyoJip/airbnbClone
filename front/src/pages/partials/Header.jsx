import React, { useState } from "react";
import styles from "../../scss/pages/Header.module.scss";
import classNames from "classnames/bind";
import { connect } from "react-redux";
import SubNav from "../../components/header/SubNav";
import LoginPage from "../LoginPage";
import UserSignupPage from "../UserSignupPage";
import { Link } from "react-router-dom";

const cx = classNames.bind(styles);

const Header = props => {
	const [subNavIsVisible, setSubNavIsVisible] = useState(false);
	const [loginPageIsVisible, setLoginPageIsVisible] = useState(false);
	const [SignupPageIsVlisible, setSignupPageIsVisible] = useState(false);

	const onClickSubNav = () => setSubNavIsVisible(prev => !prev);
	const onClickLoginLink = () => setLoginPageIsVisible(true);
	const onClickSignupLink = () => setSignupPageIsVisible(true);

	const profile =
		props.user.isLoggedIn && props.user.profile ? (
			<img src="" alt="프로필이미지" />
		) : (
			<i className={cx("fas", "fa-user-circle", "profile_img")}></i>
		);

	return (
		<header className={cx("body_header")}>
			<ul className={cx("nav_list")}>
				<li className={cx("airbnb_icon")}>
					<Link to="/">
						<i className={cx("fab", "fa-airbnb")}></i>airbnb
					</Link>
				</li>
				<li className={cx("room_search")}>
					<input
						className={cx("search_input")}
						type="text"
						id="keyword"
						placeholder="검색 시작하기"
					/>
					<i className={cx("fas", "fa-search")} id="searchBtn"></i>
				</li>
				<li className={cx("header_profile")} id="userNav" onClick={onClickSubNav}>
					<i className={cx("fas", "fa-bars")}></i>
					{profile}
					{subNavIsVisible && (
						<SubNav
							onClickLoginLink={onClickLoginLink}
							onClickSignupLink={onClickSignupLink}
						/>
					)}
				</li>
			</ul>
			{loginPageIsVisible && (
				<LoginPage
					actions={props.actions}
					onClickExitBtn={() => setLoginPageIsVisible(false)}
					onClickSignupLink={onClickSignupLink}
				/>
			)}
			{SignupPageIsVlisible && (
				<UserSignupPage
					actions={props.actions}
					onClickExitBtn={() => setSignupPageIsVisible(false)}
					onClickLoginLink={onClickLoginLink}
				/>
			)}
		</header>
	);
};

const mapStateToProps = state => {
	return {
		user: state,
	};
};

export default connect(mapStateToProps)(Header);
