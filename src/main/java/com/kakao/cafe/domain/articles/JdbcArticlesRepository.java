package com.kakao.cafe.domain.articles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class JdbcArticlesRepository implements ArticlesRepository {

	private final DataSource dataSource;

	public JdbcArticlesRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<Articles> findAll() {
		String sql = "select article_id,title,contents,user_id,created_date "
					+ "from articles";
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			pstmt = connection.prepareStatement(sql);
			rs = pstmt.executeQuery();
			List<Articles> articlesList = new ArrayList<>();
			while (rs.next()) {
				Articles article = new Articles();
				article.setArticleId(rs.getLong("article_id"));
				article.setTitle(rs.getString("title"));
				article.setContents(rs.getString("contents"));
				article.setWriter(rs.getString("user_id"));
				article.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());

				articlesList.add(article);
			}
			return articlesList;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
			close(connection, pstmt, rs);
		}
	}

	@Override
	public void save(Articles article) {
		String sql = "insert into articles(title,contents,user_id,created_date) "
						+ "values(?,?,?,now())";
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			connection = getConnection();
			pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, article.getTitle());
			pstmt.setString(2, article.getContents());
			pstmt.setString(3, article.getWriter());
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();

			if (rs.next()) {
				article.setArticleId(rs.getLong("article_id"));
			} else {
				throw new SQLException("articleId 조회 실패");
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
			close(connection, pstmt, rs);
		}
	}

	@Override
	public Articles findByArticleId(long articleId) {
		String sql = "select article_id,title,contents,user_id,created_date "
					+ "from articles "
					+ "where article_id = ?";
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			pstmt = connection.prepareStatement(sql);
			pstmt.setLong(1, articleId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				Articles article = new Articles();
				article.setTitle(rs.getString("title"));
				article.setContents(rs.getString("contents"));
				article.setWriter(rs.getString("user_id"));
				article.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());

				return article;
			} else {
				throw new NullPointerException();
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
			close(connection, pstmt, rs);
		}
	}

	private Connection getConnection() {
		return DataSourceUtils.getConnection(dataSource);
	}

	private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
			if (conn != null) {
				close(conn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void close(Connection conn) throws SQLException {
		DataSourceUtils.releaseConnection(conn, dataSource);
	}
}
