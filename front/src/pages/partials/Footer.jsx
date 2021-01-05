import React from "react";
import classNames from "classnames/bind";
import styles from "../../scss/pages/Footer.module.scss";

const cx = classNames.bind(styles);

const Footer = () => {
	return (
		<footer className={cx("body_footer")}>
			<div className={cx("footer_wrap")}>
				<p className={cx("company_info")}>
					<span className={cx("right")}>© 2020 Airbnb, Inc. All rights reserved</span>
					<span className={cx("project")}>airbnb clone project</span>
				</p>
				<p className={cx("page_config")}>
					<i className="fas fa-globe-asia"></i>
					<span className={cx("language")}>한국어(KR)</span>
					<small>￦</small>
					<span className="currency">KRW</span>
					<a target={cx("blank")} href="https://github.com/HyoJip/airbnbClone">
						<i className={cx("fab", "fa-github")}></i>
					</a>
					<a target={cx("blank")} href="https://www.airbnb.co.kr/">
						<i className={cx("fab", "fa-airbnb")}></i>
					</a>
				</p>
			</div>
		</footer>
	);
};

export default Footer;
