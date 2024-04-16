import "./Navigation.css";
import { Link } from "react-router-dom";
import { nav_list } from "./navigation_list";

export default function Navigation({ currentPage }) {
    return (
        <>
            <nav className="navigation">
                <div className="navigation-wrapper">
                    <ul className="navigation-list">
                        {nav_list.map((nav, index) => (
                            <Link key={index} to={nav.to}>
                                <li
                                    className={
                                        "navigation-list__item link-hover-horizontal " +
                                        (currentPage === nav.page ? "active" : "")
                                    }
                                >
                                    {nav.title}
                                </li>
                            </Link>
                        ))}
                    </ul>
                </div>
            </nav>
        </>
    );
}
