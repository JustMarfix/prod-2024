from typing import Union

from fastapi import FastAPI, APIRouter, Depends
from passlib.context import CryptContext
from sqlalchemy.orm import Session
from starlette.responses import Response

from database.database_connector import get_session
from database.models import DBUser
from models import PingResponse, AuthSignInPostResponse, AuthSignInPostRequest, ErrorResponse, AuthRegisterPostResponse, \
    AuthRegisterPostRequest, UserProfile
from tools.auth import create_access_token

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


def verify_password(plain_password, hashed_password):
    return pwd_context.verify(plain_password, hashed_password)


def get_password_hash(password):
    return pwd_context.hash(password)


app = FastAPI(
    title='SMM app API',
    version='1.0',
)

router = APIRouter(prefix='/api')


@router.get('/ping', response_model=PingResponse)
def ping() -> PingResponse:
    return PingResponse(status="ok")


@router.post(
    '/auth/sign-in',
    response_model=Union[AuthSignInPostResponse, ErrorResponse],
    responses={
        '200': {'model': AuthSignInPostResponse},
        '401': {'model': ErrorResponse},
    },
)
def auth_sign_in(response: Response, body: AuthSignInPostRequest,
                 db_session: Session = Depends(get_session)) -> Union[AuthSignInPostResponse, ErrorResponse]:
    user = db_session.query(DBUser).filter(DBUser.login == body.login).first()
    if user is None or not verify_password(body.password, user.password):
        response.status_code = 401
        return ErrorResponse(reason="user not found")
    token = create_access_token({"login": user.login})
    return AuthSignInPostResponse(token=token)


@router.post(
    '/auth/register',
    response_model=Union[AuthRegisterPostResponse, ErrorResponse],
    responses={
        '201': {'model': AuthRegisterPostResponse},
        '400': {'model': ErrorResponse},
        '409': {'model': ErrorResponse},
    },
    response_model_exclude_none=True,
)
def auth_register(
        response: Response, body: AuthRegisterPostRequest, db_session: Session = Depends(get_session)
) -> Union[AuthRegisterPostResponse, ErrorResponse]:
    db_model = DBUser(**body.dict())
    db_model.password = get_password_hash(db_model.password)
    db_session.add(db_model)
    try:
        db_session.commit()
    except:
        response.status_code = 409
        return ErrorResponse(reason="conflict")
    response.status_code = 201
    return AuthRegisterPostResponse(profile=UserProfile(**db_model.dict()))


app.include_router(router)
