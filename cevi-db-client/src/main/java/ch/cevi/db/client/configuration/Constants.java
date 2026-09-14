package ch.cevi.db.client.configuration;

public class Constants {
	public static final int CONNECTION_TIME_OUT = 5000;

	/**
	 * Hitobito's OAuth redirect_uri must be {@code https://} except on
	 * Hitobito's own "integration" deployments, where {@code
	 * http://localhost} is additionally allowed -- neither of which a
	 * distributed desktop app can offer. {@code urn:ietf:wg:oauth:2.0:oob}
	 * is Doorkeeper's documented fallback for exactly this case: the
	 * authorization code is displayed on the Hitobito page instead of being
	 * redirected anywhere, and the user pastes it back into the app.
	 *
	 * @see <a href=
	 *      "https://github.com/hitobito/hitobito/blob/master/doc/developer/people/oauth.md">Hitobito
	 *      OAuth documentation</a>
	 */
	public static final String OAUTH_REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

	/**
	 * {@code api} covers every JSON:API endpoint; {@code with_roles} is
	 * needed so {@code /oauth/profile} returns the logged-in person's id
	 * (see {@link ch.cevi.db.client.communication.OAuthLoginFlow#fetchProfile}).
	 * The OAuth application registered on the Hitobito instance must permit
	 * both scopes.
	 */
	public static final String OAUTH_SCOPE = "api with_roles";

	// @formatter:off
	public final static String ABOUT_DIALOG_LICENSE = "The MIT License (MIT)\n" + "Copyright (c) 2016 Marc Baumgartner\n\n"
			+ "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\n\n"
			+ "The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software."
			+ "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.";
	// @formatter:on
}
