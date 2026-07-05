from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):

    mongo_uri: str
    database_name: str

    jwt_secret_key: str
    jwt_algorithm: str

    access_token_expire_minutes: int

    admin_name: str
    admin_email: str
    admin_password: str
    admin_phone: str

    model_config = SettingsConfigDict(
        env_file=".env",
        extra="ignore"
    )

settings = Settings()